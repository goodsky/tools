import argparse
import itertools
import json
import os

from balatro_save_reader import BalatroCard, BalatroSaveReader
from poker_hands import find_poker_hands, poker_hand_names

def main(max_discard: int, verbose: bool = False):
    save_file = 'Balatro\\1\\save.jkr'
    save_path = os.path.join(os.getenv('APPDATA'), save_file)
    save = BalatroSaveReader(save_path)

    hand = save.hand()
    deck = save.deck()

    print('Current Hand:', *hand)
    print('Deck has', len(deck), 'cards remaining.')

    stats = brute_force_2(hand, deck, max_discard)
    if verbose:
        print(json.dumps(stats, indent=3))
    return stats

#
# Brute Force Attempt #2 --- less recursion --- 
#
def brute_force_2(hand: list[BalatroCard], deck: list[BalatroCard], max_discard: int):
    deck_draw_combinations = precompute_deck_draw_combinations(len(deck), max_discard) 
    stats = {}

    for discard_count in range(1, max_discard + 1):
        for discard_indices in itertools.combinations(range(len(hand)), discard_count):
            discard_stats = { hand_name: 0 for hand_name in poker_hand_names}
            for draw_indices in deck_draw_combinations[discard_count]:
                swap_cards(hand, deck, discard_indices, draw_indices)
                update_discard_stats(discard_stats, find_poker_hands(hand))
                swap_cards(hand, deck, discard_indices, draw_indices)
            stats[indices_to_int(discard_indices)] = discard_stats

    return stats


def find_poker_hands_stub(hand: list[BalatroCard]):
    # what if we do something extremely fast here instead?
    # A: We can get 4 discards down to ~
    return { hand_name: 0 for hand_name in poker_hand_names}


def precompute_deck_draw_combinations(deck_length: int, max_draw: int) -> dict[int, list[list[int]]]:
    return { draw_count: list(itertools.combinations(range(deck_length), draw_count)) for draw_count in range(1, max_draw + 1)}


def swap_cards(hand: list[BalatroCard], deck: list[BalatroCard], discard_indices: list[int], draw_indices: list[int]) -> None:
    for i in range(len(discard_indices)):
        discard_index = discard_indices[i]
        draw_index = draw_indices[i]

        discard = hand[discard_index]
        hand[discard_index] = deck[draw_index]
        deck[draw_index] = discard


def update_discard_stats(stats: dict[str, int], poker_hands: dict[str, bool]) -> None:
    for hand_name, is_found in poker_hands.items():
        if is_found:
            stats[hand_name] += 1


def indices_to_int(indices: list[int]) -> int:
    value = 0
    for index in indices:
        value = value | (1 << index)
    return value

#
# First attempt at brute force --- recursion all day long ---
#
def brute_force_1(hand: list[BalatroCard], deck: list[BalatroCard], max_discard: int):
    hand_discard_mask = [False for _ in range(len(hand))]
    deck_draw_mask = [False for _ in range(len(deck))]

    reset_stats()
    discard(0, hand, deck, hand_discard_mask, deck_draw_mask, max_discard)

    return get_stats()


def discard(hand_index: int, hand: list[BalatroCard], deck: list[BalatroCard], hand_discard_mask: list[bool], deck_draw_mask: list[bool], discards_remaining: int):
    if hand_index >= len(hand) or discards_remaining == 0:
        return
    
    # Try discarding this card
    hand_discard_mask[hand_index] = True
    discarded_card = hand[hand_index]
    for drawn_card in draw(deck, deck_draw_mask):
        hand[hand_index] = drawn_card

        poker_hands = find_poker_hands(hand)
        update_stats(hand_discard_mask, poker_hands)
        
        discard(hand_index + 1, hand, deck, hand_discard_mask, deck_draw_mask, discards_remaining - 1)

    hand[hand_index] = discarded_card
    hand_discard_mask[hand_index] = False

    # Try not discarding this card
    discard(hand_index + 1, hand, deck, hand_discard_mask, deck_draw_mask, discards_remaining)


def draw(deck: list[BalatroCard], deck_draw_mask: list[bool]):
    for deck_index in range(len(deck)):
        if deck_draw_mask[deck_index]:
            continue

        deck_draw_mask[deck_index] = True
        yield deck[deck_index]
        deck_draw_mask[deck_index] = False


global_stats = {}
def reset_stats():
    global global_stats
    global_stats = {}


def get_stats():
    global global_stats
    return global_stats


def update_stats(discard_mask: list[bool], poker_hands: dict[str, bool]):
    global global_stats
    mask_id = mask_to_int(discard_mask)

    if mask_id not in global_stats:
        global_stats[mask_id] = {}
    
    for hand_name, is_found in poker_hands.items():
        if hand_name not in global_stats[mask_id]:
            global_stats[mask_id][hand_name] = 0
        global_stats[mask_id][hand_name] += 1 if is_found else 0


def mask_to_int(mask: list[bool]):
    value = 0
    for i in range(len(mask)):
        value += (1 if mask[i] else 0) << i
    return value


def int_to_mask(value: int, mask_length: int):
    mask = [False for _ in range(mask_length)]
    mask_index = 0
    while value > 0:
        if value % 2 == 1:
            mask[mask_index] = True
        value /= 2
        mask_index += 1
    return mask


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description = 'Get Statistics for the game Balatro')
    parser.add_argument('max_discard', help='Maximum number of discards to brute force', default=3)
    args = parser.parse_args()
    main(int(args.max_discard))