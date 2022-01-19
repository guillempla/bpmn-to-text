class LaneSet:

    def __init__(self, id, name):
        self.id = id
        self.name = name
        self.lanes = []

    def getId(self):
        return self.id

    def getName(self):
        return self.name

    def getLanes(self):
        return self.lanes

    def setId(self, id):
        self.id = id

    def setName(self, name):
        self.name = name

    def setLanes(self, lanes):
        self.lanes = lanes
