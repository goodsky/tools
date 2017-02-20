using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace ImageSorter
{
    public class RecoveryState
    {
        // OutOfMemoryException! Hacky restore file to avoid losing work.
        public static readonly string RestoreFile = ".hackyrestore";

        public List<int> SelectionIndices;
        public ImageSet[] ImageSets;

        public RecoveryState(List<int> selectionIndices, ImageSet[] imageSets)
        {
            this.SelectionIndices = selectionIndices;
            this.ImageSets = imageSets;
        }

        public static string Serialize(RecoveryState recoveryState)
        {
            var str = new StringBuilder();
            str.AppendLine(string.Join(",", recoveryState.SelectionIndices));
            str.AppendLine(string.Join<ImageSet>(";", recoveryState.ImageSets));
            return str.ToString();
        }

        public static RecoveryState Deserialize(string data)
        {
            using (var sr = new StringReader(data))
            {
                var selectionIndices = sr.ReadLine().Split(',').Select(s => int.Parse(s)).ToList();
                var imageSets = sr.ReadToEnd().Split(';').Select(s => ImageSet.Deserialize(s)).ToArray();

                return new RecoveryState(selectionIndices, imageSets);
            }
        }
    }
}
