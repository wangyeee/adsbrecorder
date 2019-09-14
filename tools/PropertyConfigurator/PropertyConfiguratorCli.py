import os, sys, json, secrets, base64
from pathlib import Path
from jproperties import Properties

class PropScanner:

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

class PropertyTemplate:

    CHANGEME_VALUE_PREFIX = 'CHANGEME'

    def __init__(self, path):
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
                if val.startswith(PropertyTemplate.CHANGEME_VALUE_PREFIX) == False:
                    self.existingProps[key] = val

    def getParametersNotSet(self):
        params = {}
        for key, val in self.config.properties.items():
            if val.startswith(PropertyTemplate.CHANGEME_VALUE_PREFIX):
                changemeArray = val.split('_')
                if len(changemeArray) == 1:
                    params[key] = '' if key not in self.existingProps else self.existingProps[key]
                elif key in self.existingProps:
                    params[key] = self.existingProps[key]
                else:
                    cmd = changemeArray[1]
                    if cmd.startswith('RAND'):
                        try:
                            length = int(cmd[len('RAND'): len(cmd)])
                            params[key] = base64.b64encode(secrets.token_bytes(length)).decode('utf-8')
                        except ValueError:
                            params[key] = ''
        return params

    def getOutputPropertiesFileName(self):
        return self.destPath

    def updateExistingProperty(self, key, value):
        self.existingProps[key] = value

    def updateExistingProperties(self, props):
        self.existingProps.update(props)

    def generatePropertiesFile(self):
        p = Properties()
        p.properties.update(self.existingProps)
        with open(self.destPath, 'wb') as f:
            p.store(f, encoding='utf-8')

def scan(scanPath):
    scanner = PropScanner(scanPath)
    scanner.scan()
    templateList = []
    for tmpl in scanner.propsFileList:
        q = PropertyTemplate(tmpl)
        lst = q.getParametersNotSet()
        notSet = {}
        for l in lst:
            notSet[l] = ''
        if len(notSet) > 0:
            template = {}
            template['file'] = q.getOutputPropertiesFileName()
            template['properties'] = notSet
            templateList.append(template)
        else:
            q.generatePropertiesFile()
    with open(scanPath + os.path.sep + 'manual_config.json', 'wb') as manCfg:
        manCfg.write(json.dumps(templateList, indent=4).encode('utf-8'))
        print('Please edit manual_config.json in ' + scanPath + ' to set parameters without default values.')
        print('And then generate properties files with `python ' + os.path.basename(__file__) + ' manual_config.json`')

def generateFromManualConfig(configPath):
    with open(configPath, 'rb') as manCfg:
        configList = json.load(manCfg)
        for config in configList:
            tmplCfg = PropertyTemplate(config['file'] + '.template')
            tmplCfg.updateExistingProperties(config['properties'])
            tmplCfg.generatePropertiesFile()

def main():
    srcPath = os.path.dirname(os.path.realpath(__file__))
    if len(sys.argv) == 2:
        if os.path.isfile(sys.argv[1]):
            generateFromManualConfig(os.path.abspath(sys.argv[1]))
            return
        srcPath = os.path.abspath(sys.argv[1])
    scan(srcPath)

if __name__ == '__main__':
    main()
