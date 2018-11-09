using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace XamarinToDoApp
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ItemEntriesPage : ContentPage
    {

        public ItemEntriesPage()
        {
            InitializeComponent();

            BindingContext = new ItemEntriesViewModel(); 
        }

        private void OnItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            (sender as ListView).SelectedItem = null;
        }
    }
}