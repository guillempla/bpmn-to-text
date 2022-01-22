# Python code to parse a BPMN file
import logging
import xml.etree.ElementTree as ET
from Elements import Event, Flow, Gateway, Process, LaneSet
import os
import pickle
from pathlib import Path

# import untangle

BPMN_PATH = "../bpmn_models"
PICKLE_PATH = "../parsed_bpmn/"


def parse_xml(xml_file):
	# create element tree object
	tree = ET.parse(xml_file)

	# get root element
	root = tree.getroot()
	logging.debug(root.tag)
	logging.debug("\n")

	processes = []

	# iterate BPMN items
	for item in root:
		if item.tag.__contains__('process'):

			p = Process(item.attrib['id'])
			logging.debug("     Process:    ", p.getId())

			# empty events, flows dictionary
			events = []
			gateways = []
			flows = []

			# iterate child elements of item
			for child in item:
				try:
					child.attrib['id']
				except KeyError:
					continue
				if child.tag.__contains__('sequenceFlow'):
					f = Flow(child.attrib['id'], child.attrib['sourceRef'], child.attrib['targetRef'])
					logging.debug("         Sequence Flow: ", f.getId())
					logging.debug("         Incoming:      ", f.getIncoming())
					logging.debug("         Outcoming:     ", f.getOutgoing())
					flows.append(f)
				elif child.tag.__contains__('exclusiveGateway'):
					try:
						name = child.attrib['name']
					except KeyError:
						name = "NA"
					try:
						direction = child.attrib['gatewayDirection']
					except KeyError:
						direction = "NA"
					g = Gateway(child.attrib['id'], name, direction)
					logging.debug("         Gateway:       ", g.getId())
					logging.debug("         Name:          ", g.getName())
					logging.debug("         Direction:     ", g.getGatewayDirection())
					gateways.append(g)
				elif child.tag.__contains__('laneSet'):
					try:
						name = child.attrib['name']
					except KeyError:
						name = "NA"
					l = LaneSet(child.attrib['id'], name)
					logging.debug("         LaneSet:       ", l.getId())
					logging.debug("         Name:          ", l.getName())
				else:
					try:
						name = child.attrib['name']
					except KeyError:
						name = "NA"
					e = Event(child.attrib['id'], name)
					logging.debug("         Event:         ", e.getId())
					logging.debug("         Name:          ", e.getName())
					events.append(e)

				logging.debug("\n")

			p.setEvents(events)
			p.setGateways(gateways)
			p.setFlows(flows)
			processes.append(p)
			logging.debug("\n")

	# return BPMN elements list
	return processes


def save_model_as_pickle(file_name, model):
	name_file = PICKLE_PATH + file_name + '.txt'
	with open(name_file, 'wb') as fh:
		pickle.dump(model, fh)


def read_bpmn_files():
	"""
	Finds all BPMN files inside the DATA_PATH, parses it and stores it to an array of BpmnModel.

	:return:
	bpmn_array: Array of BpmnModel objects, each position contains the BPMN parsed into an BpmnModel object.
	"""

	bpmn_array = []

	for file in Path(BPMN_PATH).glob('**/*.bpmn'):
		print(file.name)
		model = parse_xml(os.fspath(file))
		# save_model_as_pickle(file.name, model)
		bpmn_array.append(model)

	return bpmn_array


def main():
	# parse all bpmn files
	models_parsed = read_bpmn_files()


if __name__ == "__main__":
	# calling main function
	main()
