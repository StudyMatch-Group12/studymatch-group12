import React from 'react';
import './App.css';
import RegistoEstudante from './components/RegistoEstudante';
import StudentProfile from './pages/StudentProfile';

function App() {
    const path = window.location.pathname;

    if (path === '/student-profile') {
        return <StudentProfile />;
    }

    return (
        <div className="App">
            <RegistoEstudante />
        </div>
    );
}

export default App;