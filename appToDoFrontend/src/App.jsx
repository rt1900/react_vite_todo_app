import { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import Header from './components/Header';  
import AddNoteComponent from './components/AddNoteComponent';
import NoteModal from './components/NoteModal';

// Здесь определяем функцию parseJwt
const parseJwt = (token) => {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Failed to parse JWT:", e);
        return null;
    }
};
function App() {

  const [notes, setNotes] = useState([]);   //+
  const [selectedNote, setSelectedNote] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [userEmail, setUserEmail] = useState('');


    /*    useEffect(() => {
            const token = localStorage.getItem('token');

            setLoading(true);
            if (token) {
                setIsAuthenticated(true);
                axios.get('http://localhost:8080/api/notes', {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                })
                    .then(response => {
                        setNotes(response.data);
                    })
                    .catch(error => {
                        console.error('Error fetching notes:', error);
                        setIsAuthenticated(false);
                    })
            .finally(() => {
                    setLoading(false); // Завершаем загрузку
                });
            } else {
                setIsAuthenticated(false);
                setLoading(false);
                console.error('No token found, unable to fetch notes.');
            }
        }, []);*/

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            setIsAuthenticated(true); // Устанавливаем аутентификацию
            loadNotes(); // Загружаем заметки, если есть токен
        } else {
            setIsAuthenticated(false); // Сбрасываем аутентификацию, если токена нет
        }
    }, []);


    const loadNotes = () => {
        const token = localStorage.getItem('token');

        setLoading(true);
        if (token) {
            axios
                .get('http://localhost:8080/api/notes', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                .then((response) => {
                    setNotes(response.data);
                })
                .catch((error) => {
                    console.error('Error fetching notes:', error);
                    setIsAuthenticated(false);
                })
                .finally(() => {
                    setLoading(false); // Завершаем загрузку
                });
        } else {
            setIsAuthenticated(false);
            setLoading(false);
            console.error('No token found, unable to fetch notes.');
        }
    };

  // Изменен handleAddNote для отправки запроса на сервер
  const handleAddNote = () => {
      const token = localStorage.getItem('token');
    const newNote = { title: '', text: '', isCompleted: false };    // Убираем создание ID на фронтенде
    axios.post('http://localhost:8080/api/notes', newNote , {
        headers: {
            Authorization: `Bearer ${token}`
        }
    })    // Отправляем новую заметку на сервер
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
      const token = localStorage.getItem('token');
    axios.delete(`http://localhost:8080/api/notes/${id}`, {
        headers: {
            Authorization: `Bearer ${token}`, // Добавляем токен в заголовок Authorization
            'Content-Type': 'application/json' // Указываем тип содержимого
        }
    })
      .then(() => {
        setNotes(notes.filter(note => note.id !== id));
      })
      .catch(error => {
        console.error('Error deleting note:', error);
      });
  };

    // Изменен handleSaveNote для отправки запроса на сервер
    const handleSaveNote = (updatedNote) => {
        const token = localStorage.getItem('token');
      axios.put(`http://localhost:8080/api/notes/${updatedNote.id}`, updatedNote, {
          headers: {
              Authorization: `Bearer ${token}`, // Добавляем токен в заголовок Authorization
              'Content-Type': 'application/json' // Указываем тип содержимого
          }
      })
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
      const token = localStorage.getItem('token');
    
      axios.put(`http://localhost:8080/api/notes/${id}`, updatedNote, {
          headers: {
              Authorization: `Bearer ${token}`, // Добавляем токен в заголовок Authorization
              'Content-Type': 'application/json' // Указываем тип содержимого
          }
      })
        .then(response => {
          setNotes(notes.map(note => (note.id === id ? response.data : note)));
        })
        .catch(error => {
          console.error('Error updating note completion status:', error);
        });
    };

    const handleRegister = ({ email, password }, onClose) => {
        console.log('handleRegister called with:', { email, password });
        const username = email;
        axios.post('http://localhost:8080/api/register',
            { username, email, password },
            { headers: { 'Content-Type': 'application/json' } }
        )
            .then(response => {
                console.log('Registration successful:', response.data);
                alert('Registration successful! Please log in to continue.');
                onClose();
            })
            .catch(error => {
                console.error('Error registering:', error);
            });
    };

    const handleLogin = ({ email, password }) => {
        axios.post('http://localhost:8080/api/login',
            { email, password },
            { headers: { 'Content-Type': 'application/json' } }
        )
            .then(response => {
                console.log('Login successful:', response.data);
                setIsAuthenticated(true);
                if (response.data.token) {
                    localStorage.setItem('token', response.data.token);
                    const decodedToken = parseJwt(response.data.token);
                    if (decodedToken && decodedToken.sub) {
                        setUserEmail(decodedToken.sub); // Устанавливаем email пользователя
                    }
                    loadNotes(); // Загрузка заметок сразу после входа в систему
                } else {
                    console.error('Token not found in response');
                }
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
        setNotes([]); // Очищаем заметки
        localStorage.removeItem('token'); // Удаляем токен из локального хранилища
    };

    if (loading) {
        return <div>Loading...</div>; // Показать индикатор загрузки
    }


  return (
      <div>
        <Header
            handleAddNote={handleAddNote}
            onSignin={handleSignin}
            onLogin={handleLogin}
            onLogout={handleLogout}
            isAuthenticated={isAuthenticated}
            handleRegister={handleRegister}
            userEmail={userEmail} // Передаем email в Header

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
