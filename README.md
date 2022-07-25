# Improved Huffman coding

Modified Huffman coding that tries to behave as an adaptive one (this is NOT a so-called "Adaptive Huffman coding", but it would be it if this phrase hadn't already been reserved). This is something that does not exist (at least I did not find something like this except for the above-mentioned Adaptive Huffman coding that uses different technique, so it's not the same), so it was sort of an improvisation from my side.

# Results

**Briefly**: Unlike normal Huffman coding that works well for static letter frequencies only, **Improved Huffman coding** works well for ***both*** static and **dynamic (!)** letter frequencies. Worst-case (dynamic freq.) compression ratio for normal Huffman was around **1.06**, when in case with Improved Huffman the worst-case compression ratio was around **1.18**, which is **better than normal Huffman**. Considering that a best-case (static freq.) compression ratio differed for a negligible amount when comparing both codings (around 0.01 less for Improved Huffman), I can say that the **efforts for Improved Huffman coding paid off**.

**A bit more detailed**: see [**THIS**](https://github.com/MrPatrek/java-improved-huffman-coding/blob/main/presentation.pdf).
