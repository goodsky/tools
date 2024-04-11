import os

from balatro_save_reader import BalatroCard, BalatroSaveReader
from poker_hands import find_poker_hands

def main():
    save_file = 'Balatro\\1\\save.jkr'
    save_path = os.path.join(os.getenv('APPDATA'), save_file)
    save = BalatroSaveReader(save_path)

    hand = save.hand()
    deck = save.deck()

    print(f'Deck has {len(deck)} cards remaining.')
    print('Current Hand:')
    print(*hand)

    stats = next_hand_stats(hand, deck, max_discard=3)
    print(stats)


def next_hand_stats(hand: list[BalatroCard], deck: list[BalatroCard], max_discard: int):
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
    main()