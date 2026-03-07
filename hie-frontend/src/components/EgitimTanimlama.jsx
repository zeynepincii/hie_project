import React, { useState, useEffect } from 'react';
import { Box, Typography, Paper, TextField, Button, Grid, MenuItem, Divider } from '@mui/material';
import { Save, Email } from '@mui/icons-material';
import axios from 'axios';

export default function EgitimTanimlama() {
    // Form verilerini tuttuğumuz State
    const [egitim, setEgitim] = useState({
        egitimAdi: '',
        egitmenAdi: '',
        tarih: '',
        saat: '',
        mailIcerigi: ''
    });

    // Admin maili eliyle düzenlemeye başladı mı? (Otomatik doldurmayı durdurmak için)
    const [mailDuzenlendiMi, setMailDuzenlendiMi] = useState(false);

    const egitmenler = [
        "Prof. Dr. Ahmet Yılmaz",
        "Doç. Dr. Ayşe Demir",
        "Dekan Mehmet Vural"
    ];

    // SİHİRLİ KISIM: Yukarıdaki alanlar değiştikçe mail içeriğini otomatik oluşturur
    useEffect(() => {
        if (!mailDuzenlendiMi) {
            const egitimAdiMetni = egitim.egitimAdi || '[Eğitim Adı]';
            const tarihMetni = egitim.tarih ? egitim.tarih.split('-').reverse().join('.') : '[Tarih]';
            const saatMetni = egitim.saat || '[Saat]';
            const egitmenMetni = egitim.egitmenAdi || '[Eğitmen]';

            const otomatikMail = `Değerli Personelimiz,\n\nÜniversitemiz Personel Daire Başkanlığı tarafından düzenlenen "${egitimAdiMetni}" konulu hizmet içi eğitimimiz, ${tarihMetni} tarihinde saat ${saatMetni} itibarıyla ${egitmenMetni} eğitmenliğinde gerçekleştirilecektir.\n\nEğitime katılımınız beklenmektedir.\nİyi çalışmalar dileriz.`;

            setEgitim(prev => ({ ...prev, mailIcerigi: otomatikMail }));
        }
    }, [egitim.egitimAdi, egitim.egitmenAdi, egitim.tarih, egitim.saat, mailDuzenlendiMi]);

    // Admin mail alanına elle müdahale ederse
    const mailiElleDuzenle = (e) => {
        setMailDuzenlendiMi(true); // Artık otomatik doldurmayı durdur
        setEgitim({ ...egitim, mailIcerigi: e.target.value });
    };

    const formGonder = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');

            // Backend'in beklediği formata (LocalDateTime) çeviriyoruz
            const baslangicTarihiBirlesik = `${egitim.tarih}T${egitim.saat}:00`;

            // Arka uca gönderilecek paket
            const gidenVeri = {
                egitimAdi: egitim.egitimAdi,
                egitmenAdi: egitim.egitmenAdi,
                baslangicTarihi: baslangicTarihiBirlesik,
                bitisTarihi: baslangicTarihiBirlesik, // Backend hata vermesin diye şimdilik aynısını gönderiyoruz
                // mailIcerigi: egitim.mailIcerigi -> İleride backend'e mail içeriğini de kaydedebiliriz!
            };

            await axios.post('http://localhost:8080/api/v1/egitimler', gidenVeri, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            alert("Harika! Eğitim ve mail taslağı başarıyla kaydedildi.");

            // Formu tamamen sıfırla
            setEgitim({ egitimAdi: '', egitmenAdi: '', tarih: '', saat: '', mailIcerigi: '' });
            setMailDuzenlendiMi(false);

        } catch (error) {
            console.error("Kayıt hatası:", error);
            alert("Eğitim kaydedilirken bir hata oluştu.");

            // Hatanın tam röntgenini çekiyoruz
            const hataKodu = error.response ? error.response.status : 'Bilinmiyor';
            const hataMesaji = error.response ? JSON.stringify(error.response.data) : error.message;

            console.error("Detaylı Kayıt Hatası:", hataMesaji);
            alert(`Sunucu Hatası (${hataKodu})! Lütfen tarayıcı konsoluna (F12) bakın.`);
        }



    };

    return (
        <Paper elevation={3} sx={{ p: 4, borderRadius: 2 }}>
            <Typography variant="h5" fontWeight="bold" color="primary" gutterBottom>Yeni Eğitim Tanımla</Typography>
            <Box component="form" onSubmit={formGonder} sx={{ mt: 3 }}>
                <Grid container spacing={3}>
                    {/* Üst Kısım: Form Alanları */}
                    <Grid item xs={12} md={6}>
                        <TextField required fullWidth label="Eğitim Adı"
                                   value={egitim.egitimAdi} onChange={(e) => setEgitim({...egitim, egitimAdi: e.target.value})} />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField required fullWidth select label="Eğitmen Seçiniz"
                                   value={egitim.egitmenAdi} onChange={(e) => setEgitim({...egitim, egitmenAdi: e.target.value})}>
                            {egitmenler.map((isim, index) => <MenuItem key={index} value={isim}>{isim}</MenuItem>)}
                        </TextField>
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField required fullWidth label="Eğitim Günü" type="date"
                                   InputLabelProps={{ shrink: true }}
                                   value={egitim.tarih} onChange={(e) => setEgitim({...egitim, tarih: e.target.value})} />
                    </Grid>
                    <Grid item xs={12} md={6}>
                        <TextField required fullWidth label="Eğitim Saati" type="time"
                                   InputLabelProps={{ shrink: true }}
                                   value={egitim.saat} onChange={(e) => setEgitim({...egitim, saat: e.target.value})} />
                    </Grid>

                    {/* Alt Kısım: Mail Şablonu */}
                    <Grid item xs={12}>
                        <Divider sx={{ my: 2 }} />
                        <Typography variant="subtitle1" fontWeight="bold" sx={{ mb: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Email color="action" /> Eğitim Davet Maili (Personele Gönderilecek)
                        </Typography>
                        <TextField
                            fullWidth
                            multiline
                            rows={6}
                            variant="outlined"
                            value={egitim.mailIcerigi}
                            onChange={mailiElleDuzenle}
                            helperText="Bu metin yukarıdaki bilgilere göre otomatik oluşur. İsterseniz elle müdahale edip değiştirebilirsiniz."
                            sx={{ backgroundColor: '#f9fbe7' }}
                        />
                    </Grid>

                    {/* Buton */}
                    <Grid item xs={12} sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                        <Button type="submit" variant="contained" color="primary" size="large" startIcon={<Save />}>
                            Eğitimi Kaydet
                        </Button>
                    </Grid>
                </Grid>
            </Box>
        </Paper>
    );
}