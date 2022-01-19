class Lane:

    def __init__(self, id, name):
        self.id = id
        self.name = name
        self.flowNodes = []

    def getId(self):
        return self.id

    def getName(self):
        return self.name

    def getFlowNodes(self):
        return self.flowNodes

    def setId(self, id):
        self.id = id

    def setName(self, name):
        self.name = name

    def setFlowNodes(self, flowNodes):
        self.flowNodes = flowNodes
