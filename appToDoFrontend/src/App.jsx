import { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import Header from './components/Header';  
import AddNoteComponent from './components/AddNoteComponent';
import NoteModal from './components/NoteModal';




function App() {

  const [notes, setNotes] = useState([]);   //+
  const [selectedNote, setSelectedNote] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);



    useEffect(() => {                                               //1
    // Получаем все заметки при загрузке компонента
    axios.get('http://localhost:8080/api/notes')
      .then(response => {
        setNotes(response.data);
      })
      .catch(error => {
        console.error('Error fetching notes:', error);
      });
  }, []);                                                        //1



  // Изменен handleAddNote для отправки запроса на сервер
  const handleAddNote = () => {
    const newNote = { title: '', text: '', isCompleted: false };    // Убираем создание ID на фронтенде
    axios.post('http://localhost:8080/api/notes', newNote)    // Отправляем новую заметку на сервер
      .then(response => {
        setNotes([...notes, response.data]);     // Сервер возвращает заметку с сгенерированным ID
      })
      .catch(error => {
        console.error('Error creating note:', error);
      });
  };

  const handleNoteClick = (note) => {
    setSelectedNote(note);
  };




  // Изменен handleDeleteNote для отправки запроса на сервер
  const handleDeleteNote = (id) => {
    axios.delete(`http://localhost:8080/api/notes/${id}`)
      .then(() => {
        setNotes(notes.filter(note => note.id !== id));
      })
      .catch(error => {
        console.error('Error deleting note:', error);
      });
  };

    // Изменен handleSaveNote для отправки запроса на сервер
    const handleSaveNote = (updatedNote) => {
      axios.put(`http://localhost:8080/api/notes/${updatedNote.id}`, updatedNote)
        .then(response => {
          setNotes(notes.map(note => (note.id === updatedNote.id ? response.data : note)));
          setSelectedNote(null);
        })
        .catch(error => {
          console.error('Error updating note:', error);
        });
    };


    const handleToggleComplete = (id, isCompleted) => {
      const noteToUpdate = notes.find(note => note.id === id);
      const updatedNote = { ...noteToUpdate, isCompleted };
    
      axios.put(`http://localhost:8080/api/notes/${id}`, updatedNote)
        .then(response => {
          setNotes(notes.map(note => (note.id === id ? response.data : note)));
        })
        .catch(error => {
          console.error('Error updating note completion status:', error);
        });
    };

    const handleRegister = ({ email, password, captcha }) => {
        axios.post('http://localhost:8080/api/register', { email, password, captcha })
            .then(response => {
                console.log('Registration successful:', response.data);
                setIsAuthenticated(true);
            })
            .catch(error => {
                console.error('Error registering:', error);
            });
    };



    const handleLogin = ({ email, password }) => {
        axios.post('http://localhost:8080/api/login', { email, password })
            .then(response => {
                console.log('Login successful:', response.data);
                setIsAuthenticated(true);
            })
            .catch(error => {
                console.error('Error logging in:', error);
            });
    };

    const handleSignin = () => {
        // Логика для обработки регистрации
    };

    const handleLogout = () => {
        setIsAuthenticated(false);
    };


  return (
      <div>
        <Header
            handleAddNote={handleAddNote}
            onSignin={handleSignin}
            onLogin={handleLogin}
            onLogout={handleLogout}
            isAuthenticated={isAuthenticated}
            handleRegister={handleRegister}
        />

        <AddNoteComponent
            notes={notes} //+
            handleDeleteNote={handleDeleteNote}//+
            handleNoteClick={handleNoteClick} //+
            handleToggleComplete={handleToggleComplete}
        />

        {selectedNote && (
            <NoteModal
                note={selectedNote}
                onSave={handleSaveNote}
                onClose={() => setSelectedNote(null)}
            />
        )}
        {/*<img src="/finger.png" alt="Pointing Finger" className="pointing-image"/>*/}
      </div>
  );
}

export default App;
