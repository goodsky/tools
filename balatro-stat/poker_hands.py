from balatro_save_reader import BalatroCard, BalatroPokerHand

def has_straight(hand: list[BalatroCard], require_flush: bool = False) -> bool:
    # nb: requires hand to be sorted by ordinal descending
    straight_size = 5
    hand_size = len(hand)
    for index in range(hand_size - straight_size + 1):
        last_card = hand[index]
        count = 1
        for next_index in range(index + 1, hand_size):
            if hand_size - next_index + count < straight_size: # there aren't enough cards to complete the straight
                break

            cur_card = hand[next_index]
            if cur_card.ordinal < last_card.ordinal - 1: # missing a value to complete the straight
                break

            if cur_card.ordinal == last_card.ordinal - 1 and (not require_flush or cur_card.suit == last_card.suit):
                count += 1
                last_card = cur_card
                if count == 5:
                    return True
    return False

def has_of_a_kind(value_counts: dict[str, int], set_size: int) -> bool:
    return any(count >= set_size for count in value_counts.values())

def has_flush(suit_counts: dict[str, int], flush_size: int = 5) -> bool:
    return any(count >= flush_size for count in suit_counts.values())

def has_pairs(value_counts: dict[str, int], pair_count: int) -> bool:
    pairs = len([count for count in value_counts.values() if count >= 2])
    return pairs >= pair_count

def has_full_house(value_counts: dict[str, int]) -> bool:
    pairs = len([count for count in value_counts.values() if count >= 2])
    threes = len([count for count in value_counts.values() if count >= 3])
    return threes >= 1 and pairs >= 2 # one of the pairs is the three of a kind 

def find_poker_hands(hand: list[BalatroCard]):
    value_counts = {}
    for card in hand:
        if card.value not in value_counts:
            value_counts[card.value] = 0
        value_counts[card.value] += 1

    suit_counts = {}
    for card in hand:
        if card.suit not in suit_counts:
            suit_counts[card.suit] = 0
        suit_counts[card.suit] += 1

    hand.sort(reverse=True, key=lambda c: c.ordinal)
    return {
        'straight_flush': has_straight(hand, require_flush=True),
        'four_of_a_kind': has_of_a_kind(value_counts, set_size=4),
        'full_house': has_full_house(value_counts),
        'flush': has_flush(suit_counts),
        'straight': has_straight(hand),
        'two_pair': has_pairs(value_counts, pair_count=2),
        'three_of_a_kind': has_of_a_kind(value_counts, set_size=3),
        'pair': has_pairs(value_counts, pair_count=1),
    }

