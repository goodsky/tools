using System.IO;
using System.Text;

namespace ImageSorter
{
    /// <summary>
    /// Set of images that are sorted within the array. To be merged with another ImageSet.
    /// </summary>
    public class ImageSet
    {
        // Current Index for this image set
        public int Index { get; set; }

        // Array of all images in this image set
        public string[] Images { get; set; }

        public override string ToString()
        {
            return ImageSet.Serialize(this);
        }

        public static string Serialize(ImageSet imageSet)
        {
            var str = new StringBuilder();
            str.AppendLine(imageSet.Index.ToString());
            str.AppendLine(string.Join(",", imageSet.Images));
            return str.ToString();
        }

        public static ImageSet Deserialize(string data)
        {
            using (var sr = new StringReader(data))
            {
                var index = int.Parse(sr.ReadLine());
                var images = sr.ReadLine().Split(',');

                return new ImageSet() { Index = index, Images = images };
            }
        }
    }
}
