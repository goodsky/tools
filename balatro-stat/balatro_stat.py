import os

from balatro_save_reader import BalatroSaveReader


def main():
    save_file = 'Balatro\\1\\save.jkr'
    save_path = os.path.join(os.getenv('APPDATA'), save_file)
    save = BalatroSaveReader(save_path)

if __name__ == '__main__':
    main()