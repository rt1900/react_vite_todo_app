

import logo from '/unnamed.png';
import SigninLogin from './SigninLogin';
import LogoutButton from './LogoutButton.jsx';


export default function Header ({handleAddNote, onSignin, onLogin, onLogout, isAuthenticated, handleRegister }){


   return(
       <div className="header">

           {isAuthenticated ? (
               <LogoutButton onLogout={onLogout} />
           ) : (
               <SigninLogin onSignin={handleRegister} onLogin={onLogin} />
           )}
           <div className="header-content">
               <img src={logo} alt="Logo" className='logo'/>
               <h1 className="colorMetro">METRO.DIGITAL</h1>
           </div>

           <div >
               <h2 className="ToDoAppAndAddNote" >ToDo App</h2>
               <button className="addNoteButton" onClick={handleAddNote}>Add Note</button>
           </div>
       </div>
   )

}