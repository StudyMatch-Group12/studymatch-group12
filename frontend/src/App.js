import React, { useState } from 'react';
import './App.css';
import './pages/Students.css';
import RegistoEstudante from './components/RegistoEstudante';
import StudentList from './pages/StudentList';
import StudentProfile from './pages/StudentProfile';
import GroupingContexts from './pages/GroupingContexts';

const PAGES = {
    REGISTER: 'register',
    STUDENTS: 'students',
    PROFILE: 'profile',
    GROUPING: 'grouping',
};

function App() {
    const [page, setPage] = useState(PAGES.REGISTER);
    const [selectedStudentId, setSelectedStudentId] = useState(null);
    const [selectedContextId, setSelectedContextId] = useState(null);

    const openProfile = (studentId) => {
        setSelectedStudentId(studentId);
        setPage(PAGES.PROFILE);
    };

    const openContext = (contextId) => {
        setSelectedContextId(contextId);
    };

    return (
        <div className="App">
            <nav className="app-nav">
                <button
                    type="button"
                    className={page === PAGES.REGISTER ? 'active' : ''}
                    onClick={() => setPage(PAGES.REGISTER)}
                >
                    Register
                </button>
                <button
                    type="button"
                    className={page !== PAGES.REGISTER ? 'active' : ''}
                    onClick={() => setPage(PAGES.STUDENTS)}
                >
                    Students
                </button>
            </nav>

            {page === PAGES.REGISTER && <RegistoEstudante />}
            {page === PAGES.STUDENTS && <StudentList onSelectStudent={openProfile} />}
            {page === PAGES.PROFILE && (
                <StudentProfile studentId={selectedStudentId} onBack={() => setPage(PAGES.STUDENTS)} />
            )}
        </div>
    );
}

export default App;
