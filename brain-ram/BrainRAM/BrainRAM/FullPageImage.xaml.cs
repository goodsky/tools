using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using System.Windows.Media.Imaging;

namespace BrainRAM
{
    public partial class FullPageImage : PhoneApplicationPage
    {
        public FullPageImage()
        {
            InitializeComponent();

            this.LocalScratchpad.DataContext = ScratchpadData.Instance;
        }

        private void PhoneApplicationPage_Loaded(object sender, RoutedEventArgs e)
        {
            // Try to set the image
            try
            {
                string imageUri = string.Empty;
                NavigationContext.QueryString.TryGetValue("ImageUri", out imageUri);

                if (imageUri.Equals(string.Empty))
                {
                    this.ErrorText.Text += "Could not load image. QueryString=" + NavigationContext.QueryString;
                }
                else
                {
                    BitmapImage image = new BitmapImage();
                    image.UriSource = new Uri("/Assets/" + imageUri, UriKind.Relative);
                    this.PageImage.Source = image;
                }
            }
            catch (Exception ex)
            {
                this.ErrorText.Text = "Exception!!!\n" + ex.ToString();
            }
        }
    }
}