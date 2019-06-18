using Acr.UserDialogs;
using Realms;
using Realms.Sync;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using Xamarin.Forms;

using Credentials = Realms.Sync.Credentials;

namespace ToDoApp
{
    public class ItemEntriesViewModel : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public ICommand DeleteEntryCommand { get; private set; }

        public ICommand AddEntryCommand { get; private set; }

        public ItemEntriesViewModel()
        {
            Initialize();
        }

        private async Task Initialize()
        {
        }
    }
}