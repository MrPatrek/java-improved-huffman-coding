# Improved Huffman coding

Modified Huffman coding that tries to behave as an adaptive one (this is NOT a so-called "Adaptive Huffman coding", but it would be it if this phrase hadn't already been reserved). This is something that does not exist (at least I did not find something like this except for the above-mentioned Adaptive Huffman coding that uses different technique, so it's not the same), so it was sort of an improvisation from my side.

# Results

**Briefly**: Unlike normal Huffman coding, **Improved Huffman coding** works well for ***both*** static letter frequencies and **dynamic (!)** letter frequencies. Worst-case compression ratio for normal Huffman is around **1.06**, when in case with Improved Huffman the worst-case compression ratio is **1.18**, which is **better than normal Huffman**, so the efforts paid off.

**A bit more detailed**: see [**THIS**](https://github.com/MrPatrek/java-improved-huffman-coding/blob/main/presentation.pdf).
