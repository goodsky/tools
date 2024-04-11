import json

from balatro_save_file import BalatroSaveFile, LiteralStruct, MapStruct, MapEntryStruct, MapKeyStruct, MapValueStruct

suit_to_char = {
    'Clubs': '♣',
    'Diamonds': '♦',
    'Hearts': '♥',
    'Spades': '♠',
}

value_to_char = {
    'Jack': 'J',
    'Queen': 'Q',
    'King': 'K',
    'Ace': 'A',
}

value_to_ordinal = {
    'Ace': 14,
    'King': 13,
    'Queen': 12,
    'Jack': 11,
    '10': 10,
    '9': 9,
    '8': 8,
    '7': 7,
    '6': 6,
    '5': 5,
    '4': 4,
    '3': 3,
    '2': 2,
}

value_to_chips = {
    'Ace': 11,
    'King': 11,
    'Queen': 11,
    'Jack': 11,
    '10': 10,
    '9': 9,
    '8': 8,
    '7': 7,
    '6': 6,
    '5': 5,
    '4': 4,
    '3': 3,
    '2': 2,
}

class BalatroCard(object):
    def __init__(self, index, card_entry):
        self.index = index
        self.suit = card_entry['base']['suit']
        self.value = card_entry['base']['value']
        self.chips = value_to_chips[self.value] + card_entry['ability']['bonus']
        self.mult = card_entry['ability']['mult']
        self.ordinal = value_to_ordinal[self.value]

        # True if the card isn't used to determine poker hands (like a Stone card)
        label = card_entry.get('label')
        self.exclude_in_poker_hand = label in [ 'Stone Card' ]
    
    def __str__(self):
        suit_str = suit_to_char[self.suit] if self.suit in suit_to_char else self.suit
        value_str = value_to_char[self.value] if self.value in value_to_char else self.value
        return f'{value_str}{suit_str}'


class BalatroPokerHand(object):
    def __init__(self, name, hand_entry):
        self.name = name
        self.chips = hand_entry['chips']
        self.mult = hand_entry['mult']

    def __init__(self, chips, mult):
        self.chips = chips
        self.mult = mult

    def __str__(self):
        return json.dumps(self.__dict__)


class BalatroSaveReader(object):
    def __init__(self, save_file_path):
        self.balatro_save_file = BalatroSaveFile(save_file_path)
        self.data = struct_to_obj(self.balatro_save_file.structs[1])

    def game_mode(self):
        return self.data['STATE']
    
    def hand(self) -> list[BalatroCard]:
        hand = self.data['cardAreas'].get('hand')
        if hand == None:
            raise Exception('Could not read a hand in save file!')
        hand_list = [BalatroCard(id, card) for id, card in hand['cards'].items()]
        hand_list.sort(key=lambda c: int(c.index))
        return hand_list
    
    def deck(self) -> list[BalatroCard]:
        deck = self.data['cardAreas'].get('deck')
        if deck == None:
            raise Exception('Could not read deck in save file!')
        return [BalatroCard(id, card) for id, card in deck['cards'].items()]
    
    def poker_hands(self) -> list[BalatroPokerHand]:
        poker_hands = self.data['GAME']['hands']
        return [BalatroPokerHand(name, poker_hand) for name, poker_hand in poker_hands.items() if poker_hand['visible']]

def struct_to_obj(cur):
    if isinstance(cur, LiteralStruct):
        if cur.structs[0] == '"': # when [0] = '"' then structs [1] is the string value
            return cur.structs[1]
        return convert_to_literal_if_possible(cur.structs[0])
    if isinstance(cur, MapStruct):
        return { struct_to_obj(child.structs[0]): struct_to_obj(child.structs[1]) for child in cur.structs if isinstance(child, MapEntryStruct)}
    if isinstance(cur, MapKeyStruct):
        return struct_to_obj(cur.structs[1]) # [0] = '"'; [1] = Key Literal
    if isinstance(cur, MapValueStruct):
        return struct_to_obj(cur.structs[0]) # this will be either a MapStruct or a LiteralStruct
    raise Exception(f"Unknown struct type! {type(cur)}")

def convert_to_literal_if_possible(val):
    try:
        return int(val)
    except ValueError:
        pass

    try:
        return float(val)
    except ValueError:
        pass

    if val == 'true':
        return True
    
    if val == 'false':
        return False

    return val