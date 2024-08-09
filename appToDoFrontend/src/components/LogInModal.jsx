import { useState } from 'react';

export default function LoginModal({ onClose, onLogin }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    //const [captcha, setCaptcha] = useState('')
    const handleSubmit = (e) => {
        e.preventDefault();
        onLogin({ email, password });
    };
    return (
        <div className="SignInModal">
            <div className="signin-modal-content">
                <h2>Login</h2>
                <form onSubmit={handleSubmit}>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required />
                    <button type="submit">Register</button>
                </form>
                <button onClick={onClose}>Close</button>
            </div>
        </div>
    );
}
