# Freeling client. Read sentences from a file, sends them to Freeling, and saves to disk the
# syntactic analysis.
import argparse
from pathlib import Path
import sys

from freelingClient import FreelingClient


def get_arguments():
    # Initialize parser
    parser = argparse.ArgumentParser()

    # Adding optional argument
    parser.add_argument("-l", "--lang",
                        help="Freeling's language. Default: 'en'",
                        default='en')
    parser.add_argument("-p", "--parsedpath",
                        help="Path where parsed bpmn are stored. Default: '../parsed_bpmn/'",
                        default='../parsed_bpmn/')
    parser.add_argument("-dh", "--host",
                        help="Freeling's Docker host. Default: '172.17.0.2'",
                        default='172.17.0.2')
    parser.add_argument("-lp", "--langport",
                        help="Language's Docker port. Default: '50005'",
                        default='50005')
    parser.add_argument("-fp", "--freelingport",
                        help="Freeling's Docker port. Default: '60006'",
                        default='60006')
    parser.add_argument("-r", "--resppath",
                        help="Path where Freeling's responses are stored. Default: '../freeling_responses/'",
                        default='../freeling_responses/')

    # Read arguments from command line
    return parser.parse_args()


def main():
    args = get_arguments()

    for file_path in Path(args.parsedpath).glob('**/*.txt'):
        client = FreelingClient(file_path, args.host, args.langport, args.freelingport, args.lang, args.resppath)
        client.send_parsed_texts()


if __name__ == '__main__':
    main()
