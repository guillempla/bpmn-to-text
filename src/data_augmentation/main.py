import os
from pathlib import Path
import xml.etree.ElementTree as ET

DATA_PATH = "../../bpmn"


def read_bpmn_files():
    """
    Finds all BPMN files inside the DATA_PATH, parses it to an ElementTree and stores it to an
    array of ElementTrees.

    :return:
    tree_array: Array of XML Trees, each position contains the Tree representation of an BPMN.
    """

    tree_array = []

    count = 0

    for file in Path(DATA_PATH).glob('**/*.bpmn'):
        tree = ET.parse(os.fspath(file))
        tree_array.append(tree)

        count += 1
        if (count == 1):
            break

    return tree_array


def main():
    parsed_files = read_bpmn_files()

    for tree in parsed_files:
        for child in tree.getroot():
            print(child.tag, child.attrib)


if __name__ == "__main__":
    main()
