import os, sys, json
from pathlib import Path

from jproperties import Properties
from PyQt5.QtCore import QObject, QThread, pyqtSignal
from PyQt5.QtWidgets import (QApplication, QFileDialog, QGridLayout,
                             QHBoxLayout, QHeaderView, QLabel, QLineEdit,
                             QMainWindow, QMessageBox, QPushButton,
                             QTableWidget, QTableWidgetItem, QVBoxLayout,
                             QWidget)

class PropScanner(QObject):

    def __init__(self, folder):
        self.folder = folder
        self.propsFileList = []
        self.excludePath = [
            os.path.sep + 'target' + os.path.sep + 'classes' + os.path.sep,
            os.path.sep + 'target' + os.path.sep + 'test-classes' + os.path.sep
        ]

    def scan(self):
        pathList = Path(self.folder).glob('**/*.properties.template')
        for path in pathList:
            pathStr = str(path)
            if self.includePath(pathStr):
                self.propsFileList.append(pathStr)

    def includePath(self, path):
        for excl in self.excludePath:
            if excl in path:
                return False
        return True

class PropertyTemplate(QObject):

    def __init__(self, path, parent = None):
        super().__init__(parent)
        self.config = Properties()
        self.path = path
        self.destPath = path[0:len(path)-len('.template')]
        with open(path, 'rb') as f:
            self.config.load(f, 'utf-8')
        self.existingProps = {}
        self.__checkExistingProps(self.destPath)

    def __checkExistingProps(self, extPropsPath):
        try:
            existConfig = Properties()
            with open(extPropsPath, 'rb') as f:
                existConfig.load(f, 'utf-8')
                self.existingProps.update(existConfig.properties)
        except FileNotFoundError:
            for key, val in self.config.properties.items():
                if val != 'CHANGEME':
                    self.existingProps[key] = val

    def getParametersNotSet(self):
        params = {}
        for key, val in self.config.properties.items():
            if val == 'CHANGEME':
                params[key] = '' if key not in self.existingProps else self.existingProps[key]
        return params

    def updateExistingProperty(self, key, value):
        self.existingProps[key] = value

    def updateExistingProperties(self, props):
        self.existingProps.update(props)

    def generatePropertiesFile(self):
        p = Properties()
        p.properties.update(self.existingProps)
        with open(self.destPath, 'wb') as f:
            p.store(f, encoding='utf-8')

class ScanPathSelectorWidget(QWidget):

    def __init__(self, scanPath, parent = None):
        super().__init__(parent)
        self.setLayout(QGridLayout())
        self.layout().addWidget(QLabel('Select source root path'), 0, 0, 1, 4)
        self.rootPathEdit = QLineEdit(scanPath)
        self.layout().addWidget(self.rootPathEdit, 1, 0, 1, 3)
        self.browseButton = QPushButton('Browse')
        self.browseButton.clicked.connect(self.__chooseRootPath)
        self.layout().addWidget(self.browseButton, 1, 3, 1, 1)

    def __chooseRootPath(self):
        fileName = QFileDialog.getExistingDirectory(self, 'Choose root path')
        if fileName != None and len(fileName) > 0:
            self.rootPathEdit.setText(fileName)

    def getRootPath(self):
        return self.rootPathEdit.text()

class BackgroundScanner(QThread):
    newPropertyFileFound = pyqtSignal(object, object)
    scanComplete = pyqtSignal()

    def __init__(self, rootPath, parent = None):
        super().__init__(parent)
        self.ps = PropScanner(rootPath)
        self.templateList = {}
        self.running = False

    def run(self):
        self.running = True
        self.ps.scan()
        for tmpl in self.ps.propsFileList:
            q = PropertyTemplate(tmpl)
            self.templateList[tmpl] = q
            lst = q.getParametersNotSet()
            if len(lst) > 0:
                self.newPropertyFileFound.emit(tmpl, lst)
        self.running = False
        self.scanComplete.emit()

class ParameterList(QWidget):
    HEADER = ['Name', 'Value']

    def __init__(self, parent = None):
        super().__init__(parent)
        self.setLayout(QVBoxLayout())
        self.table = QTableWidget()
        self.table.setColumnCount(len(ParameterList.HEADER))
        self.table.horizontalHeader().setSectionResizeMode(QHeaderView.Stretch)
        self.table.horizontalHeader().hide()
        self.rowNumber = 0
        self.layout().addWidget(self.table)

    def appendNewFile(self, path, paramList):
        self.table.setRowCount(self.rowNumber + 2 + len(paramList))  # expand current table size
        self.table.setSpan(self.rowNumber, 0, 1, 2)
        self.table.setItem(self.rowNumber, 0, QTableWidgetItem('Properties file: {}'.format(path)))
        self.rowNumber += 1
        self.table.setItem(self.rowNumber, 0, QTableWidgetItem(ParameterList.HEADER[0]))
        self.table.setItem(self.rowNumber, 1, QTableWidgetItem(ParameterList.HEADER[1]))
        self.rowNumber += 1
        for key, value in paramList.items():
            self.table.setItem(self.rowNumber, 0, QTableWidgetItem(key))
            self.table.setItem(self.rowNumber, 1, QTableWidgetItem(value))
            self.rowNumber += 1

    def clearContexts(self):
        while self.table.rowCount() > 0:
            self.table.removeRow(0)
        self.rowNumber = 0

class ScanResultWidget(QWidget):
    def __init__(self, parent = None):
        super().__init__(parent)
        self.setLayout(QVBoxLayout())
        self.messageLabel = QLabel('Ready')
        self.propsWidget = ParameterList()
        self.layout().addWidget(self.messageLabel)
        self.layout().addWidget(self.propsWidget)
        self.scanner = None
        self.scanCompleted = False

    def scan(self, path):
        if self.scanner == None or self.scanner.running == False:
            self.messageLabel.setText('Scanning: {}'.format(path))
            self.propsWidget.clearContexts()
            self.scanner = BackgroundScanner(path)
            self.scanner.newPropertyFileFound.connect(self.appendPropertyTemplate)
            self.scanner.scanComplete.connect(self.fetchScanFullResult)
            self.scanner.start()

    def appendPropertyTemplate(self, path, props):
        self.propsWidget.appendNewFile(path, props)

    def fetchScanFullResult(self):
        self.messageLabel.setText(self.messageLabel.text() + ', complete.')
        self.scanCompleted = True

    def generatePropertiesFiles(self):
        if self.scanner == None or self.scanner.running == True:
            return
        rowNumber = 0
        workingTmpl = None
        while rowNumber < self.propsWidget.table.rowCount():
            name = self.propsWidget.table.item(rowNumber, 0).text()
            if (name.startswith('Properties file:')):
                if workingTmpl != None:
                    workingTmpl.generatePropertiesFile()
                    del self.scanner.templateList[workingTmpl.path]
                workingTmpl = self.scanner.templateList[name[len('Properties file: '): len(name)]]
                rowNumber += 1 # Skip header "Name = Value"
            else:
                value = self.propsWidget.table.item(rowNumber, 1).text()
                workingTmpl.updateExistingProperty(name, value)
            rowNumber += 1
        for dftProps in self.scanner.templateList.values():
            dftProps.generatePropertiesFile()
        QMessageBox.information(self, 'Complete', 'Properties file generated successfully.')

    def getManualConfigParameters(self):
        if self.scanner == None or self.scanner.running == True:
            return []
        manualConfigs = []
        rowNumber = 0
        workingTmpl = None
        workingConfig = {}
        while rowNumber < self.propsWidget.table.rowCount():
            name = self.propsWidget.table.item(rowNumber, 0).text()
            if (name.startswith('Properties file:')):
                if workingTmpl != None:
                    configItem = {
                        'file': workingTmpl.path[0:len(workingTmpl.path)-len('.template')],
                        'properties': workingConfig
                    }
                    manualConfigs.append(configItem)
                    workingConfig = {}
                workingTmpl = self.scanner.templateList[name[len('Properties file: '): len(name)]]
                rowNumber += 1 # Skip header "Name = Value"
            else:
                value = self.propsWidget.table.item(rowNumber, 1).text()
                # print('{}={}'.format(name, value))
                workingConfig[name] = value
            rowNumber += 1
        return manualConfigs

class PropertyConfigurator(QMainWindow):

    def __init__(self, parent = None):
        super().__init__(parent)
        self.setWindowTitle('Property Configurator')
        self.window = QWidget()
        self.window.setLayout(QVBoxLayout())
        self.currentPath = os.path.dirname(os.path.realpath(__file__))
        self.scanPathSelector = ScanPathSelectorWidget(self.currentPath)
        self.window.layout().addWidget(self.scanPathSelector)
        self.scanResult = ScanResultWidget()
        self.window.layout().addWidget(self.scanResult)
        self.actionButtonWidget = QWidget()
        self.actionButtonWidget.setLayout(QHBoxLayout())
        self.closeButton = QPushButton('Close')
        self.scanButton = QPushButton('Scan')
        self.saveButton = QPushButton('Save')
        self.saveConfigButton = QPushButton('Save As Config')
        self.actionButtonWidget.layout().addWidget(self.closeButton)
        self.actionButtonWidget.layout().addWidget(self.saveConfigButton)
        self.actionButtonWidget.layout().addWidget(self.saveButton)
        self.actionButtonWidget.layout().addWidget(self.scanButton)
        self.saveConfigButton.clicked.connect(self.__saveManualConfigFile)
        self.closeButton.clicked.connect(self.close)
        self.saveButton.clicked.connect(self.scanResult.generatePropertiesFiles)
        self.scanButton.clicked.connect(lambda: self.scanResult.scan(self.scanPathSelector.getRootPath()))
        self.window.layout().addWidget(self.actionButtonWidget)
        self.setCentralWidget(self.window)
        self.resize(1280, 800)

    def __saveManualConfigFile(self):
        if self.scanResult.scanCompleted == False:
            QMessageBox.information(self, 'Info', 'Please scan confir template files before save as manual config file.')
        else:
            destConfig = QFileDialog.getSaveFileName(self, 'Save as manual config file', 'manual_config.json')
            if destConfig and len(destConfig[0]) > 0:
                cfg = self.scanResult.getManualConfigParameters()
                with open(destConfig[0], 'wb') as destCfg:
                    destCfg.write(json.dumps(cfg, indent=4).encode('utf-8'))

if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = PropertyConfigurator()
    window.show()
    sys.exit(app.exec_())
