class Gateway:

    def __init__(self, id, name, gatewayDirection='Unspecified'):
        self.id = id
        self.name = name.replace("\r", "").replace("\n", "")
        self.gatewayDirection = gatewayDirection

    def getId(self):
        return self.id

    def getName(self):
        return self.name

    def getGatewayDirection(self):
        return self.gatewayDirection

    def setId(self, id):
        self.id = id

    def setName(self, name):
        self.name = name

    def setGatewayDirection(self, gatewayDirection):
        self.gatewayDirection = gatewayDirection
