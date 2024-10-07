import PropTypes from 'prop-types';
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
    return (
        <div className="signin-login-buttons">
            <button className="signinButton" onClick={handleRegisterOpen}>Sign In</button>
            <button className="loginButton" onClick={handleLoginOpen}>Log In</button>
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

SigninLogin.propTypes = {
    onSignin: PropTypes.func.isRequired,
    onLogin: PropTypes.func.isRequired,
};