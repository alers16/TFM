public class COMMONS_COLLECTIONS_PERMUTATION_ITERATOR_NEXT {
/**
 * Returns the next permutation of the input collection.
 *
 * @return a list of the permutator's elements representing a permutation
 * @throws NoSuchElementException if there are no more permutations
 */
@Override
public List<E> next() {
    if (!hasNext()) {
        throw new NoSuchElementException();
    }
    // find the largest mobile integer k
    int indexOfLargestMobileInteger = -1;
    int largestKey = -1;
    for (int i = 0; i < keys.length; i++) {
        if (direction[i] && i < keys.length - 1 && keys[i] > keys[i + 1] || !direction[i] && i > 0 && keys[i] > keys[i - 1]) {
            if (keys[i] > largestKey) {
                // NOPMD
                largestKey = keys[i];
                indexOfLargestMobileInteger = i;
            }
        }
    }
    if (largestKey == -1) {
        final List<E> toReturn = nextPermutation;
        nextPermutation = null;
        return toReturn;
    }
    // swap k and the adjacent integer it is looking at
    final int offset = direction[indexOfLargestMobileInteger] ? 1 : -1;
    final int tmpKey = keys[indexOfLargestMobileInteger];
    keys[indexOfLargestMobileInteger] = keys[indexOfLargestMobileInteger + offset];
    keys[indexOfLargestMobileInteger + offset] = tmpKey;
    final boolean tmpDirection = direction[indexOfLargestMobileInteger];
    direction[indexOfLargestMobileInteger] = direction[indexOfLargestMobileInteger + offset];
    direction[indexOfLargestMobileInteger + offset] = tmpDirection;
    // reverse the direction of all integers larger than k and build the result
    final List<E> nextP = new ArrayList<>();
    for (int i = 0; i < keys.length; i++) {
        if (keys[i] > largestKey) {
            direction[i] = !direction[i];
        }
        nextP.add(objectMap.get(Integer.valueOf(keys[i])));
    }
    final List<E> result = nextPermutation;
    nextPermutation = nextP;
    return result;
}
}

