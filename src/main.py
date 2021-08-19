import os
from pathlib import Path
from src.bpmn_parser import BpmnModel, UserFormMessage

DATA_PATH = "../bpmn"


def read_bpmn_files():
    """
    Finds all BPMN files inside the DATA_PATH, parses it and stores it to an array of BpmnModel.

    :return:
    bpmn_array: Array of BpmnModel objects, each position contains the BPMN parsed into an BpmnModel object.
    """

    bpmn_array = []

    for file in Path(DATA_PATH).glob('**/*.bpmn'):
        model = BpmnModel(os.fspath(file))
        bpmn_array.append(model)

    return bpmn_array


def main():
    print(Path.cwd())

    parsed_files = read_bpmn_files()

    print(parsed_files[0])


if __name__ == "__main__":
    main()
