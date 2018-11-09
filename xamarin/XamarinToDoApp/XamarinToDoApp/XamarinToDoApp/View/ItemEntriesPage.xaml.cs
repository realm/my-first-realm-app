using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace XamarinToDoApp
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ItemEntriesPage : ContentPage
    {

        public ItemEntriesPage(ItemEntriesViewModel viewModel)
        {
            InitializeComponent();

            BindingContext = viewModel; 
            viewModel.Navigation = Navigation;
        }

        void OnItemTapped(object sender, ItemTappedEventArgs e)
        {
          //  (BindingContext as ItemEntriesViewModel).EditEntry((Entries)e.Item);
        }

        void OnItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            (sender as ListView).SelectedItem = null;
        }
    }
}