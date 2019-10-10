using Realms;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace ToDoApp
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ItemEntriesPage : ContentPage
    {

        public ItemEntriesPage(Realm realm)
        {
            InitializeComponent();

            BindingContext = new ItemEntriesViewModel(Navigation, realm);
        }

        protected override bool OnBackButtonPressed()
        {
            // Ignore hardware back button. There is a "log out" button instead.
            return true;
        }

        private void OnItemSelected(object sender, SelectedItemChangedEventArgs e)
        {
            (sender as ListView).SelectedItem = null;
        }
    }
}