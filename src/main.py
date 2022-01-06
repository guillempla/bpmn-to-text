import os
import pickle
from pathlib import Path
from src.bpmn_parser import BpmnModel

BPMN_PATH = "../bpmn"
PICKLE_PATH = "../parsed_bpmn/"

def save_model_as_picke(file_name, model):
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
        model = BpmnModel(os.fspath(file))
        save_model_as_picke(file.name, model)
        bpmn_array.append(model)

    return bpmn_array


def main():
    print(Path.cwd())

    parsed_files = read_bpmn_files()


if __name__ == "__main__":
    main()
