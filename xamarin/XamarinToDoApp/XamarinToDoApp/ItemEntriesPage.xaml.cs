using System;
using System.Collections.Generic;

using Xamarin.Forms;

namespace XamarinToDoApp
{
    public partial class ItemEntriesPage : ContentPage
    {
        public ItemEntriesPage()
        {
            InitializeComponent();

        }
        void OnItemTapped(object sender, ItemTappedEventArgs e)
        {
            
        }

         void OnItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            (sender as ListView).SelectedItem = null;
        }
    }
}