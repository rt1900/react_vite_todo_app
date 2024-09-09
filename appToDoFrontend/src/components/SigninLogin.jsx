import SignInModal from './SignInModal';
import { useState } from 'react';
import LoginModal from './LogInModal';

export default function SigninLogin({ onSignin, onLogin }) {

    const [isRegisterModalOpen, setRegisterModalOpen] = useState(false);
    const [isLoginModalOpen, setLoginModalOpen] = useState(false);
    const handleRegisterOpen = () => {
        setRegisterModalOpen(true);
    };
    const handleRegisterClose = () => {
        setRegisterModalOpen(false);
    };
    const handleLoginOpen = () => {
        setLoginModalOpen(true);
    };
    const handleLoginClose = () => {
        setLoginModalOpen(false);
    };
    const handleRegister = (data) => {
        console.log('Registration data:', data);
        handleRegisterClose();
    };
    const handleLogin = (data) => {
        console.log('Login data:', data);
        handleLoginClose();
    };
    return (
        <div className="signin-login-buttons">
            <button className="signinlogin signinButton" onClick={handleRegisterOpen}>Sign In</button>
            <button className="signinlogin loginButton" onClick={handleLoginOpen}>Log In</button>
            {isRegisterModalOpen &&
                <SignInModal
                    onClose={handleRegisterClose}
                    onRegister={onSignin}
                />
            }
            {isLoginModalOpen &&
                <LoginModal
                    onClose={handleLoginClose}
                    onLogin={onLogin}
                />
            }
        </div>
    );
}