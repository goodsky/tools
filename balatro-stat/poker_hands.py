from balatro_save_reader import BalatroCard

class PokerHandName:
    STRAIGHT_FLUSH = 'Straight Flush'
    FOUR_OF_A_KIND = 'Four of a Kind'
    FULL_HOUSE = 'Full House'
    FLUSH = 'Flush'
    STRAIGHT = 'Straight'
    THREE_OF_A_KIND = 'Three of a Kind'
    TWO_PAIR = 'Two Pair'
    PAIR = 'Pair'
    HIGH_CARD = 'High Card'


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

def find_poker_hands(hand: list[BalatroCard]) -> dict[str, bool]:
    hand = [card for card in hand if not card.exclude_in_poker_hand]

    value_counts = {}
    suit_counts = {}
    for card in hand:
        if card.value not in value_counts:
            value_counts[card.value] = 0
        value_counts[card.value] += 1

        if card.suit not in suit_counts:
            suit_counts[card.suit] = 0
        suit_counts[card.suit] += 1   

    hand.sort(reverse=True, key=lambda c: c.ordinal)
    return {
        PokerHandName.STRAIGHT_FLUSH: has_straight(hand, require_flush=True),
        PokerHandName.FOUR_OF_A_KIND: has_of_a_kind(value_counts, set_size=4),
        PokerHandName.FULL_HOUSE: has_full_house(value_counts),
        PokerHandName.FLUSH: has_flush(suit_counts),
        PokerHandName.STRAIGHT: has_straight(hand),
        PokerHandName.THREE_OF_A_KIND: has_of_a_kind(value_counts, set_size=3),
        PokerHandName.TWO_PAIR: has_pairs(value_counts, pair_count=2),
        PokerHandName.PAIR: has_pairs(value_counts, pair_count=1),
        PokerHandName.HIGH_CARD: True,
    }

