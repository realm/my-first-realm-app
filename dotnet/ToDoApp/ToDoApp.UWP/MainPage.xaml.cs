namespace ToDoApp.UWP
{
    public sealed partial class MainPage
    {
        public MainPage()
        {
            this.InitializeComponent();

            LoadApplication(new ToDoApp.App());
        }
    }
}
