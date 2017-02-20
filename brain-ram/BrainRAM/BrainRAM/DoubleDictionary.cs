using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BrainRAM
{
    /// <summary>
    /// A duel-way dictionary class.
    /// Used for de-coding and encoding ciphers and encodings
    /// </summary>
    public class DoubleDictionary<S, T>
    {
        private Dictionary<S, T> encode = new Dictionary<S, T>();
        private Dictionary<T, S> decode = new Dictionary<T, S>();

        public void AddEncoding(S original, T encoded)
        {
            encode.Add(original, encoded);
            decode.Add(encoded, original);
        }

        public T Encode(S s)
        {
            return encode[s];
        }

        public S Decode(T s)
        {
            return decode[s];
        }
    }
}
