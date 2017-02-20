using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Windows.Forms;

namespace ImageSorter
{
    public partial class SelectImages : Form
    {
        private List<ImageSet> _imageSets = new List<ImageSet>();

        public SelectImages()
        {
            InitializeComponent();
        }

        /// <summary>
        /// Add an ImageSet to the list.
        /// </summary>
        /// <param name="sender">Not used</param>
        /// <param name="e">Not used</param>
        private void ImageSetsAddButton_Click(object sender, EventArgs e)
        {
            var fileDialog = new OpenFileDialog();
            fileDialog.Multiselect = true;
            fileDialog.ShowDialog(this);

            var images = fileDialog.FileNames;

            if (images.Length == 0)
                return;

            var rootPath = default(string);
            foreach (var image in images)
            {
                if (rootPath == default(string))
                {
                    rootPath = Path.GetDirectoryName(image);
                }
                else if (rootPath != Path.GetDirectoryName(image))
                {
                    rootPath = "Multiple Directories";
                    break;
                }
            }

            _imageSets.Add(new ImageSet() { Images = images });
            this.ImageSetsList.Items.Add($"{images.Length} files in directory {rootPath}");
        }

        /// <summary>
        /// Remove an ImageSet from the list.
        /// </summary>
        /// <param name="sender">Not used</param>
        /// <param name="e">Not used</param>
        private void ImageSetsRemoveButton_Click(object sender, EventArgs e)
        {
            if (this.ImageSetsList.SelectedIndices.Count == 0)
                return;

            // Multiselect is false
            var selectedIndex = this.ImageSetsList.SelectedIndices[0];

            _imageSets.RemoveAt(selectedIndex);
            this.ImageSetsList.Items.RemoveAt(selectedIndex);
        }

        /// <summary>
        /// Move to the ImageSorter
        /// </summary>
        /// <param name="sender">Not used</param>
        /// <param name="e">Not used</param>
        private void DoneButton_Click(object sender, EventArgs e)
        {
            if (this.ImageSetsList.Items.Count < 2)
            {
                return;
            }

            var imageSorter = new ImageSorter(_imageSets);
            this.Visible = false;
            imageSorter.ShowDialog();
            this.Close();
        }

        /// <summary>
        /// Restore from a crashed run.
        /// </summary>
        /// <param name="sender">Not used</param>
        /// <param name="e">Not used</param>
        private void RestoreButton_Click(object sender, EventArgs e)
        {
            if (!File.Exists(RecoveryState.RestoreFile))
            {
                MessageBox.Show("No recovery data exists!");
                return;
            }

            RecoveryState state;
            using (var fin = new StreamReader(RecoveryState.RestoreFile))
            {
                state = RecoveryState.Deserialize(fin.ReadToEnd());
            }

            var imageSorter = new ImageSorter(state.ImageSets.ToList(), state.SelectionIndices);
            this.Visible = false;
            imageSorter.ShowDialog();
            this.Close();
        }
    }
}
