

import logo from '/unnamed.png'


export default function Header ({handleAddNote}){


   return(
       <div className="header">

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