using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Reflection;
using System.Windows.Forms;

namespace ImageSorter
{
    public partial class ImageSorter : Form
    {
        private const int IconWidth = 250;
        private const int IconHeight = 250;

        private int _totalImages;
        private ImageSet[] _imageSets;

        private Image _end;
        private Image _red;

        // list of the selection indexes that have been chosen in the order they were chosen
        // this will be used to construct the final list in the last step
        private List<int> _selectionIndices = new List<int>();

        /// <summary>
        /// Create an image sorter for the provided list of image sets
        /// </summary>
        /// <param name="imageSets">The list of image sets</param>
        /// <param name="selectionIndices">Option parameter for crash recovery</param>
        public ImageSorter(List<ImageSet> imageSets, List<int> selectionIndices = null)
        {
            _imageSets = imageSets.ToArray();

            if (selectionIndices != null)
            {
                _selectionIndices = selectionIndices;
            }

            try
            {
                var endImageStream = Assembly.GetExecutingAssembly().GetManifestResourceStream("ImageSorter.end.png");
                _end = Image.FromStream(endImageStream);

                var redImageStream = Assembly.GetExecutingAssembly().GetManifestResourceStream("ImageSorter.red.png");
                _red = Image.FromStream(redImageStream);
            }
            catch
            {
                MessageBox.Show("Error loading embedded resource in assembly. Program corrupt!");
            }

            InitializeComponent();

            // Create a dynamic image list that will update as we click on images
            var imageList = new ImageList();
            imageList.ImageSize = new Size(IconWidth, IconHeight);

            // Manually initialize the SorterListView to do what we want
            this.SorterListView.LargeImageList = imageList;
            this.SorterListView.DoubleClick += SorterListView_DoubleClick;

            // Load the first images
            int i = 0;
            foreach (var imageSet in _imageSets)
            {
                _totalImages += imageSet.Images.Length;

                imageList.Images.Add(LoadImage(imageSet.Images[imageSet.Index]));
                this.SorterListView.Items.Add(Path.GetFileName(imageSet.Images[imageSet.Index]), i++);
            }

            this.UpdateStatus(0);
        }

        /// <summary>
        /// Select an image and add it to the 
        /// </summary>
        /// <param name="sender">Not used</param>
        /// <param name="e">Not useds</param>
        private void SorterListView_DoubleClick(object sender, EventArgs e)
        {
            // Multiselect is false
            var index = this.SorterListView.SelectedIndices[0];

            var imageSet = _imageSets[index];
            var imageList = this.SorterListView.LargeImageList;

            if (imageSet.Index == imageSet.Images.Length)
                return;

            imageSet.Index++;
            _selectionIndices.Add(index);
            this.UpdateStatus(_selectionIndices.Count);

            if (imageSet.Index == imageSet.Images.Length)
            {
                this.SorterListView.Items[index].Text = string.Empty;
                SwapImage(imageList.Images, index, _end);
            }
            else
            {
                this.SorterListView.Items[index].Text = Path.GetFileName(imageSet.Images[imageSet.Index]);
                SwapImage(imageList.Images, index, LoadImage(imageSet.Images[imageSet.Index]));
            }
        }

        /// <summary>
        /// You made a mistake! Undo the last step.
        /// </summary>
        /// <param name="sender">Not used</param>
        /// <param name="e">Not used</param>
        private void UndoButton_Click(object sender, EventArgs e)
        {
            int selectionCount = _selectionIndices.Count - 1;
            if (selectionCount < 0)
                return;

            var index = _selectionIndices[selectionCount];

            // remove the selection from the list and push back the image set index
            _selectionIndices.RemoveAt(selectionCount);
            _imageSets[index].Index--;

            // Update the view list to the new image
            var imageSet = _imageSets[index];
            var imageList = this.SorterListView.LargeImageList;
            this.SorterListView.Items[index].Text = Path.GetFileName(imageSet.Images[imageSet.Index]);
            SwapImage(imageList.Images, index, LoadImage(imageSet.Images[imageSet.Index]));

            this.UpdateStatus(_selectionIndices.Count);
        }

        /// <summary>
        /// Select the Output Folder path.
        /// </summary>
        /// <param name="sender">Not used</param>
        /// <param name="e">Not used</param>
        private void OutputFolderButton_Click(object sender, EventArgs e)
        {
            var folderdialog = new FolderBrowserDialog();
            folderdialog.ShowDialog(this);

            this.OutputFolderTextBox.Text = folderdialog.SelectedPath;
        }

        /// <summary>
        /// Done. Output all the images.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DoneButton_Click(object sender, EventArgs e)
        {
            var outputFolder = this.OutputFolderTextBox.Text;
            var prefix = this.OutputPrefixTextBox.Text;
            if (string.IsNullOrEmpty(outputFolder))
            {
                MessageBox.Show("Output Folder not set!");
                return;
            }

            int[] outputIndex = new int[_imageSets.Length];

            int i = 0;
            foreach (int selection in _selectionIndices)
            {
                var selectedFile = _imageSets[selection].Images[outputIndex[selection]++];
                File.Copy(selectedFile, Path.Combine(outputFolder, $"{prefix}{i++}{Path.GetExtension(selectedFile)}"));
            }

            MessageBox.Show("Done!");
            this.Close();
        }

        /// <summary>
        /// Update the status message
        /// </summary>
        /// <param name="imagesSorted">Number of images sorted</param>
        private void UpdateStatus(int imagesSorted)
        {
            this.StatusLabel.Text = $"{imagesSorted} of {_totalImages} images sorted";

            using (var fout = new StreamWriter(RecoveryState.RestoreFile))
            {
                fout.WriteLine(RecoveryState.Serialize(new RecoveryState(_selectionIndices, _imageSets)));
            }
        }

        /// <summary>
        /// Swap images in the ImageList
        /// </summary>
        /// <param name="collection">The image list image collection</param>
        /// <param name="index">The index of the image to swap</param>
        /// <param name="file">The new file to use</param>
        private void SwapImage(ImageList.ImageCollection collection, int index, Image image)
        {
            var original = collection[index];
            collection[index] = image;
            original.Dispose();
        }

        /// <summary>
        /// Load and resize a file
        /// </summary>
        /// <param name="file">Path to file on disk.</param>
        /// <returns>The image in the correct size.</returns>
        private Image LoadImage(string file)
        {
            try
            {
                using (var original = Image.FromFile(file))
                {
                    var rotate = original.GetPropertyItem(0x112).Value[0];
                    switch (rotate)
                    {
                        case 3:
                            original.RotateFlip(RotateFlipType.Rotate180FlipNone);
                            break;
                        case 6:
                            original.RotateFlip(RotateFlipType.Rotate90FlipNone);
                            break;
                        case 8:
                            original.RotateFlip(RotateFlipType.Rotate270FlipNone);
                            break;
                    }

                    var scaled = new Bitmap(IconWidth, IconHeight);
                    using (Graphics graphics = Graphics.FromImage(scaled))
                    {
                        graphics.DrawImage(original, 0, 0, IconWidth, IconHeight);
                    }

                    return scaled;
                }
            }
            catch (OutOfMemoryException)
            {
                MessageBox.Show($"Image {Path.GetFileName(file)} is too large to load!");
                return _red;
            }
            catch (IOException)
            {
                MessageBox.Show($"Image {Path.GetFileName(file)} could not be found!");
                return _red;
            }

        }
    }
}
