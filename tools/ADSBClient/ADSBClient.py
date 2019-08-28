import json
import os
import socket
import sys
import time
import urllib.parse
import urllib.request
from datetime import datetime

from sbs1 import SBS1Message, TransmissionType

ENCODING = 'utf-8'

class JSONSBS1Message(SBS1Message):

    def __init__(self, sbs1Message):
        super().__init__(sbs1Message)
        self.jsonArray = {
            'recordID' : None,
            'addressICAO' : 0,
            'flight' : None,
            'latitude' : 0.0,
            'longitude' : 0.0,
            'altitude' : 0,
            'velocity' : 0,
            'heading' : 0,
            'lastTimeSeen' : 0,
            'recordDate' : None,
            'sourceReceiver' : None,
            'sourceReceiverID' : None
        }
        self.missingMsgs = [
            TransmissionType.ES_AIRBORNE_POS,
            TransmissionType.ES_AIRBORNE_VEL,
            TransmissionType.ES_IDENT_AND_CATEGORY
        ]
        self.msgHooks = {
            TransmissionType.ES_AIRBORNE_POS : self.__updateAirPos,
            TransmissionType.ES_AIRBORNE_VEL : self.__updateAirVel,
            TransmissionType.ES_IDENT_AND_CATEGORY : self.__updateIDCat
        }
        if self.isValid:
            self.loggedDate = time.time()
            if self.transmissionType in self.missingMsgs:
                self.missingMsgs.remove(self.transmissionType)

    def update(self, msg):
        if msg.isValid and msg.transmissionType in self.missingMsgs and msg.icao24 == self.icao24:
            self.msgHooks[msg.transmissionType](msg)
            self.loggedDate = time.time()
            self.missingMsgs.remove(msg.transmissionType)

    def isComplete(self):
        return len(self.missingMsgs) == 0 or (
            self.altitude and self.lat and self.lon
        ) or (
            self.verticalRate and self.groundSpeed and self.track
        )

    def toJSON(self):
        self.jsonArray['addressICAO'] = int(self.icao24, 16)
        self.jsonArray['flight'] = self.callsign
        self.jsonArray['latitude'] = self.lat
        self.jsonArray['longitude'] = self.lon
        self.jsonArray['altitude'] = self.altitude
        self.jsonArray['velocity'] = self.groundSpeed
        self.jsonArray['heading'] = self.track
        self.jsonArray['verticalRate'] = self.verticalRate
        self.jsonArray['lastTimeSeen'] = int(self.loggedDate * 1000)
        self.jsonArray['recordDate'] = int(self.loggedDate * 1000)
        return self.jsonArray

    def toVelocityUpdate(self):
        vu = {}
        vu['addressICAO'] = int(self.icao24, 16)
        vu['velocity'] = self.groundSpeed
        vu['heading'] = self.track
        vu['verticalRate'] = self.verticalRate
        vu['lastTimeSeen'] = int(self.loggedDate * 1000)
        vu['recordDate'] = int(self.loggedDate * 1000)
        return vu

    def __updateAirPos(self, msg):
        self.altitude = msg.altitude
        self.lat = msg.lat
        self.lon = msg.lon
        self.alert = msg.alert
        self.emergency = msg.emergency
        self.spi = msg.spi
        self.onGround = msg.onGround

    def __updateAirVel(self, msg):
        self.groundSpeed = msg.groundSpeed
        self.track = msg.track
        self.verticalRate = msg.verticalRate
        self.alert = msg.alert
        self.emergency = msg.emergency
        self.spi = msg.spi
        self.onGround = msg.onGround

    def __updateIDCat(self, msg):
        self.callsign = msg.callsign
        self.alert = msg.alert
        self.emergency = msg.emergency
        self.spi = msg.spi
        self.onGround = msg.onGround

class SBS1Client:
    def __init__(self, host, port, sendVelocityUpdate = None, sendTrackingRecord = None):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.connect((host, port))
        self.running = False
        self.msgCount = 0
        self.flights = {}
        self.icaoCallsign = {}
        self.sendVelocityUpdate = self.__dummyConsumer if sendVelocityUpdate == None else sendVelocityUpdate
        self.sendTrackingRecord = self.__dummyConsumer if sendTrackingRecord == None else sendTrackingRecord

    def run(self):
        self.running = True
        while self.running:
            line = self.socket.recv(1024)
            if line != b'':
                msg = JSONSBS1Message(line)
                if msg.isValid:
                    msg.loggedDate = time.time()  # re-use loggedDate field
                    if msg.transmissionType == TransmissionType.ES_IDENT_AND_CATEGORY:
                        if msg.icao24 not in self.icaoCallsign:
                            self.icaoCallsign[msg.icao24] = msg.callsign
                    if msg.icao24 in self.flights:
                        self.flights[msg.icao24].update(msg)
                        if msg.transmissionType == TransmissionType.ES_AIRBORNE_VEL:
                            self.sendVelocityUpdate(self.flights[msg.icao24])
                        if self.flights[msg.icao24].isComplete():
                            if msg.icao24 in self.icaoCallsign:
                                self.flights[msg.icao24].callsign = self.icaoCallsign[msg.icao24]
                            self.msgCount += 1
                            if self.__dataCheck(self.flights[msg.icao24]):
                                self.sendTrackingRecord(self.flights[msg.icao24])
                            del self.flights[msg.icao24]
                    else:
                        self.flights[msg.icao24] = msg
        self.socket.close()

    def close(self):
        self.running = False

    def __dataCheck(self, msg):
        return msg.lat and msg.lon and msg.altitude and abs(msg.lat) > 0.0 and abs(msg.lon) > 0.0 and abs(msg.altitude) > 0.0

    def getMessageCount(self):
        return self.msgCount

    def __dummyConsumer(self, obj):
        print(obj)

class AuthenticationClient:

    def __init__(self, url, name, key):
        self.tokenExpiresTime = 0
        self.authEP = url
        self.credentials = {'name': name, 'key': key}
        self.token = None

    def getAuthorizationToken(self):
        now = datetime.timestamp(datetime.now()) * 1000
        if now > self.tokenExpiresTime:
            self.__reAuth()
        return self.token

    def __reAuth(self):
        data = urllib.parse.urlencode(self.credentials, encoding=ENCODING)
        data = data.encode(ENCODING)
        try:
            req = urllib.request.Request(self.authEP)
            req.add_header('Content-Type', 'application/x-www-form-urlencoded; charset=' + ENCODING)
            req.add_header('Content-Length', len(data))
            with urllib.request.urlopen(req, data) as response:
                token = json.loads(response.read().decode(ENCODING))
                self.token = token['token']
                self.tokenExpiresTime = int(token['expiration'])
        except urllib.error.URLError as e:
            print(e.reason)
            self.token = None

class ADSBClient:
    def __init__(self, trEP, vuEP, token, processResponse = None):
        self.trEP = trEP
        self.vuEP = vuEP
        self.token = token
        self.processResponse = self.__ignoreResponse if processResponse == None else processResponse

    def postTrackingRecord(self, msg: JSONSBS1Message):
        self.__post([msg.toJSON()], self.trEP)

    def postVelocityUpdate(self, msg: JSONSBS1Message):
        self.__post([msg.toVelocityUpdate()], self.vuEP)

    def __post(self, body, url):
        jsondata = json.dumps(body)
        jsondataasbytes = jsondata.encode(ENCODING)
        print(jsondataasbytes)
        if len(body) > 0:
            req = urllib.request.Request(url)
            req.add_header('Content-Type', 'application/json; charset=' + ENCODING)
            req.add_header('Content-Length', len(jsondataasbytes))
            req.add_header('Authorization', 'Bearer ' + self.token)
            with urllib.request.urlopen(req, jsondataasbytes) as response:
                self.processResponse(response)

    def __ignoreResponse(self, response):
        print(response.read())

def main(configFile = None):
    clientConfig = None
    try:
        if configFile == None:
            print('Loading default config file')
            configFile = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'ADSBClientConfig.json')
        with open(configFile, 'r') as cfg:
            clientConfig = json.load(cfg)
    except IOError as e:
        print('Falied to load config file: ' + configFile + ', reason:', e.strerror)
        clientConfig = None

    if clientConfig != None:
        print('Authentication url:', clientConfig['adsbcfg']['loginEP'])
        print('Authentication name:', clientConfig['adsbcfg']['name'])
        key = clientConfig['adsbcfg']['key']
        print('Authentication key:', ('*' * (len(key) - 4)) + key[len(key)-4:len(key)])
        print('TrackingRecord EP:', clientConfig['adsbcfg']['trEP'])
        print('VelocityUpdate EP:', clientConfig['adsbcfg']['vuEP'])
        print('SBS1 host:', clientConfig['sbs1cfg']['host'])
        print('SBS1 port:', clientConfig['sbs1cfg']['port'])
        authTool = AuthenticationClient(url = clientConfig['adsbcfg']['loginEP'],
                                        name = clientConfig['adsbcfg']['name'],
                                        key = key)
        clientToken = authTool.getAuthorizationToken()
        if clientToken == None:
            print('Authorization failed.')
        else:
            asdbClient = ADSBClient(trEP = clientConfig['adsbcfg']['trEP'],
                                    vuEP = clientConfig['adsbcfg']['vuEP'],
                                    token = clientToken)
            sbs1Client = SBS1Client(host = clientConfig['sbs1cfg']['host'], port = clientConfig['sbs1cfg']['port'],
                                    sendVelocityUpdate = asdbClient.postVelocityUpdate,
                                    sendTrackingRecord = asdbClient.postTrackingRecord)
            sbs1Client.run()

if __name__ == '__main__':
    main(None if len(sys.argv) != 2 else sys.argv[1])
