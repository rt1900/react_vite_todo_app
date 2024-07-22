

import logo from '/unnamed.png'


export default function Header (props){ 


   return(
        <div className="header">
            <img src={logo} alt="Logo" className='logo'/>
            <div className="header-content">
                <h1 className="colorMetro">METRO.DIGITAL</h1>
            </div>
        </div>
    )
    
    }