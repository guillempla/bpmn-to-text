# Python class to parse a BPMN file
import logging
import xml.etree.ElementTree as ET
from Elements import Event, Flow, Gateway, Process, LaneSet


def parse_attrib(elem, attrib_name):
    try:
        return elem.attrib[attrib_name]
    except KeyError:
        return "NA"


def parse_bpmn(file_path):
    # create element tree object
    try:
        tree = ET.parse(file_path)
    except FileNotFoundError:
        logging.warning("File not found: " + file_path)
        return

    # get root element
    root = tree.getroot()
    logging.debug(root.tag)
    logging.debug("\n")

    processes = []

    # iterate BPMN items
    for item in root:
        if item.tag.__contains__('process'):

            process_id = parse_attrib(item, 'id')
            p = Process(process_id)
            logging.debug("     Process:    ", p.get_id())

            # empty events, flows dictionary
            events = []
            gateways = []
            flows = []
            lanes_set = []

            # iterate child elements of item
            for child in item:
                try:
                    child.attrib['id']
                except KeyError:
                    continue
                if child.tag.__contains__('sequenceFlow'):
                    id = parse_attrib(child, 'id')
                    source_ref = parse_attrib(child, 'sourceRef')
                    target_ref = parse_attrib(child, 'targetRef')
                    flow = Flow(id, source_ref, target_ref)

                    logging.debug("         Sequence Flow: ", flow.getId())
                    logging.debug("         Incoming:      ", flow.getIncoming())
                    logging.debug("         Outcoming:     ", flow.getOutgoing())

                    flows.append(flow)

                elif child.tag.__contains__('exclusiveGateway'):
                    id = parse_attrib(child, 'id')
                    name = parse_attrib(child, 'name')
                    direction = parse_attrib(child, 'direction')
                    gateway = Gateway(id, name, direction)

                    logging.debug("         Gateway:       ", gateway.getId())
                    logging.debug("         Name:          ", gateway.getName())
                    logging.debug("         Direction:     ", gateway.getGatewayDirection())

                    gateways.append(gateway)

                elif child.tag.__contains__('laneSet'):
                    id = parse_attrib(child, 'id')
                    name = parse_attrib(child, 'name')
                    lane_set = LaneSet(id, name)

                    logging.debug("         LaneSet:       ", lane_set.getId())
                    logging.debug("         Name:          ", lane_set.getName())

                    lanes_set.append(lane_set)

                else:
                    id = parse_attrib(child, 'name')
                    name = parse_attrib(child, 'name')
                    event = Event(id, name)

                    logging.debug("         Event:         ", event.getId())
                    logging.debug("         Name:          ", event.getName())

                    events.append(event)

                logging.debug("\n")

            p.set_events(events)
            p.set_gateways(gateways)
            p.set_flows(flows)
            p.set_lanes_set(lanes_set)
            processes.append(p)
            logging.debug("\n")

    # return processes list
    return processes


class BpmnModel:
    def __init__(self, name, file_path):
        self.name = name
        self.processes = parse_bpmn(file_path)
