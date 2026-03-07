import axios from 'axios';
import React, { useState } from 'react';
import { Box, Button, TextField, Typography, Paper, Container, Link, Grid } from '@mui/material';
import { useNavigate } from 'react-router-dom';


export default function Login() {
    const navigate = useNavigate();
    const [eposta, setEposta] = useState('');
    const [sifre, setSifre] = useState('');


    const girisİsleminiBaslat = async (e) => {
        e.preventDefault();

        try {
            // Postman'deki gibi POST isteği atıyoruz
            const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
                eposta: eposta,
                sifre: sifre
            });

            /// Başarılı olursa bileti ve rolü alıp tarayıcının hafızasına kaydediyoruz
            console.log("Giriş Başarılı! Gelen Bilet:", response.data.token);
            console.log("Gelen Rol:", response.data.rol); // Konsolda rolü görebilirsin

            localStorage.setItem('token', response.data.token);
            localStorage.setItem('role', response.data.rol); // <-- Rolü de kaydettik!

            alert("Harika! Sisteme başarıyla giriş yaptınız.");
            navigate('/dashboard');



        } catch (error) {
            // Hata olursa (Yanlış şifre vs.) kullanıcıya mesaj gösteriyoruz
            console.error("Giriş başarısız:", error);
            if (error.response && error.response.status === 401) {
                alert("Hata: Girdiğiniz e-posta veya şifre yanlış!");
            } else {
                alert("Sistemsel bir hata oluştu. Lütfen teknik destekle iletişime geçin.");
            }
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Paper elevation={3} sx={{ padding: 4, width: '100%', borderRadius: 2 }}>

                    <Typography component="h1" variant="h5" align="center" gutterBottom fontWeight="bold">
                        Hizmet İçi Eğitim Sistemi
                    </Typography>

                    <Typography component="h2" variant="body2" align="center" color="textSecondary" sx={{ mb: 3 }}>
                        Personel Daire Başkanlığı Giriş Paneli
                    </Typography>

                    <Box component="form" onSubmit={girisİsleminiBaslat} sx={{ mt: 1 }}>
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Kurumsal E-posta Adresi"
                            autoFocus
                            value={eposta}
                            onChange={(e) => setEposta(e.target.value)}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            label="Şifre"
                            type="password"
                            value={sifre}
                            onChange={(e) => setSifre(e.target.value)}
                        />

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                            sx={{ mt: 3, mb: 2, py: 1.5, fontSize: '1rem' }}
                        >
                            Sisteme Giriş Yap
                        </Button>

                        {/* Alt Kısım: Şifremi Unuttum ve İletişim Yönlendirmesi */}
                        <Grid container direction="column" alignItems="center" spacing={2}>
                            <Grid item xs>
                                <Link
                                    href="https://teknikdestek.yildiz.edu.tr"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    variant="body2"
                                    underline="hover"
                                >
                                    Şifremi unuttum
                                </Link>
                            </Grid>

                            <Grid item xs sx={{ mt: 2, textAlign: 'center' }}>
                                <Typography variant="body2" color="textSecondary">
                                    Kaydınız yoksa iletişime geçiniz:
                                </Typography>
                                <Link
                                    href="https://prs.yildiz.edu.tr/"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    variant="body2"
                                    fontWeight="bold"
                                    underline="hover"
                                >
                                    Personel Daire Başkanlığı
                                </Link>
                            </Grid>
                        </Grid>

                    </Box>
                </Paper>
            </Box>
        </Container>
    );
}
