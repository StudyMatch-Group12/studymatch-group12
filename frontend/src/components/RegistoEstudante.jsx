import React, { useState } from 'react';
import './RegistoEstudante.css';

const API_URL = 'http://localhost:8080/api/estudantes';

export default function RegistoEstudante() {
    const [formData, setFormData] = useState({
        nome: '',
        numeroEstudante: '',
        email: '',
        curso: 'Engenharia Informática',
        password: ''
    });

    const [mostrarPassword, setMostrarPassword] = useState(false);
    const [mensagem, setMensagem] = useState({ texto: '', tipo: '' }); // 'sucesso' ou 'erro'

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensagem({ texto: '', tipo: '' });

        try {
            const res = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });

            if (res.status === 201) {
                setMensagem({ texto: 'Estudante registado com sucesso!', tipo: 'sucesso' });
                setFormData({
                    nome: '',
                    numeroEstudante: '',
                    email: '',
                    curso: 'Engenharia Informática',
                    password: ''
                });
            } else {
                const erroTexto = await res.text();
                setMensagem({ texto: erroTexto || 'Ocorreu um erro ao registar o estudante.', tipo: 'erro' });
            }
        } catch (err) {
            setMensagem({ texto: 'Erro de ligação ao servidor. Tenta novamente.', tipo: 'erro' });
        }
    };

    return (
        <div className="studymatch-container">
            <div className="left-panel">
                <div className="bg-decor-circle"></div>

                <div className="left-header">
                    <div className="logo-box">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
                            <path d="M6 12v5c3 3 9 3 12 0v-5"/>
                        </svg>
                        <span>StudyMatch</span>
                    </div>
                </div>

                <div className="left-content">
                    <div className="tag-eyebrow">
                        <span className="line"></span>
                        <span className="text">APRENDE. PARTILHA. CRESCE.</span>
                    </div>

                    <h1 className="main-headline">Find your ideal study team</h1>

                    <p className="subtitle">
                        Liga-te a colegas do teu curso, partilha conhecimento e vai mais longe em equipa.
                    </p>

                    <div className="pills-container">
                        <div className="pill">
                            <span className="checkmark">✓</span> Colegas do teu curso
                        </div>
                        <div className="pill">
                            <span className="checkmark">✓</span> Novas perspetivas
                        </div>
                    </div>
                </div>

                <div className="left-footer">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M3 21h18M9 8h1M9 12h1M9 16h1M14 8h1M14 12h1M14 16h1M5 21V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16"/>
                    </svg>
                    <span>Feito para a comunidade académica da UPT</span>
                </div>
            </div>

            <div className="right-panel">
                {mensagem.texto && (
                    <div className={`toast-notification ${mensagem.tipo}`}>
                        <div className="toast-left">
                            <span className="toast-icon">✓</span>
                            <span>{mensagem.texto}</span>
                        </div>
                        <button className="toast-close" onClick={() => setMensagem({ texto: '', tipo: '' })}>
                            ✕
                        </button>
                    </div>
                )}

                <div className="form-wrapper">
                    <div className="form-header">
                        <div className="badge-category">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
                                <path d="M6 12v5c3 3 9 3 12 0v-5"/>
                            </svg>
                            <span>CONTA DE ESTUDANTE</span>
                        </div>
                        <h2>Criar Conta de Estudante</h2>
                        <p>Junta-te à comunidade do StudyMatch</p>
                    </div>

                    <form onSubmit={handleSubmit} className="registration-form">
                        <div className="input-group">
                            <label>Nome Completo</label>
                            <div className="input-field-wrapper">
                                <svg className="field-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                    <circle cx="12" cy="7" r="4"/>
                                </svg>
                                <input
                                    type="text"
                                    name="nome"
                                    placeholder="Introduz o teu nome completo"
                                    value={formData.nome}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>

                        <div className="input-group">
                            <label>Número de Estudante</label>
                            <div className="input-field-wrapper">
                                <svg className="field-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                                </svg>
                                <input
                                    type="text"
                                    name="numeroEstudante"
                                    placeholder="Ex.: 3000123"
                                    value={formData.numeroEstudante}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>

                        <div className="input-group">
                            <label>E-mail UPT @upt.pt</label>
                            <div className="input-field-wrapper">
                                <svg className="field-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <rect x="2" y="4" width="20" height="16" rx="2"/>
                                    <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/>
                                </svg>
                                <input
                                    type="email"
                                    name="email"
                                    placeholder="Ex.: 3000123@upt.pt"
                                    value={formData.email}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>

                        <div className="input-group">
                            <label>Curso</label>
                            <div className="input-field-wrapper">
                                <svg className="field-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                                    <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                                </svg>
                                <select
                                    name="curso"
                                    value={formData.curso}
                                    onChange={handleChange}
                                    required
                                >
                                    <option value="Engenharia Informática">Engenharia Informática</option>
                                    <option value="Gestão">Gestão</option>
                                    <option value="Direito">Direito</option>
                                    <option value="Psicologia">Psicologia</option>
                                    <option value="Economia">Economia</option>
                                </select>
                                <svg className="dropdown-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="m6 9 6 6 6-6"/>
                                </svg>
                            </div>
                        </div>

                        <div className="input-group">
                            <label>Palavra-passe</label>
                            <div className="input-field-wrapper">
                                <svg className="field-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                                </svg>
                                <input
                                    type={mostrarPassword ? 'text' : 'password'}
                                    name="password"
                                    placeholder="Cria uma palavra-passe segura"
                                    value={formData.password}
                                    onChange={handleChange}
                                    required
                                />
                                <button
                                    type="button"
                                    className="toggle-password-btn"
                                    onClick={() => setMostrarPassword(!mostrarPassword)}
                                >
                                    {mostrarPassword ? (
                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                            <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                                            <line x1="1" y1="1" x2="23" y2="23"/>
                                        </svg>
                                    ) : (
                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                            <circle cx="12" cy="12" r="3"/>
                                        </svg>
                                    )}
                                </button>
                            </div>
                            <small className="helper-text">Usa pelo menos 8 caracteres, incluindo letras e números.</small>
                        </div>

                        <p className="terms-text">
                            Ao registares-te, aceitas os <a href="#terms">Termos de Utilização</a> e a <a href="#privacy">Política de Privacidade</a>.
                        </p>

                        <button type="submit" className="submit-btn">
                            <span>Registar e Criar Perfil</span>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <line x1="5" y1="12" x2="19" y2="12"/>
                                <polyline points="12 5 19 12 12 19"/>
                            </svg>
                        </button>
                    </form>

                    <div className="form-footer-links">
                        <p>Já tens conta? <a href="/login" className="link-login">Iniciar Sessão</a></p>
                        <div className="security-note">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                            </svg>
                            <span>Os teus dados estão seguros connosco.</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}