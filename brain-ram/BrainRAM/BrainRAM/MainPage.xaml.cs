using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using BrainRAM.Resources;

namespace BrainRAM
{
    public partial class MainPage : PhoneApplicationPage
    {
        // Constructor
        public MainPage()
        {
            InitializeComponent();

            this.LocalScratchpad.DataContext = ScratchpadData.Instance;
        }

        // Navigate from Main Page
        private void CommonEncodings_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/CommonEncodings.xaml", UriKind.Relative));
        }

        private void GeneralReference_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.Navigate(new Uri("/GeneralReferences.xaml", UriKind.Relative));
        }
    }
}