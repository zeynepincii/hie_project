import React, { useState } from 'react';
import EgitimTanimlama from '../components/EgitimTanimlama';
import {
    Box, Drawer, List, ListItem, ListItemIcon, ListItemText,
    AppBar, Toolbar, Typography, Table, TableBody, TableCell,
    TableContainer, TableHead, TableRow, Paper, Checkbox,
    Button, Divider, CssBaseline
} from '@mui/material';
import {
    People, School, History, Assignment,
    Engineering, ExitToApp, Dashboard as DashboardIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';

const drawerWidth = 260;

export default function Dashboard() {
    const navigate = useNavigate();

    const [aktifMenu, setAktifMenu] = useState('Genel Bakış');
    // YENİ KOD: Artık rolü hafızadan (localStorage) okuyor
    const [userRole, setUserRole] = useState(localStorage.getItem('role') || 'PERSONEL');

    const cikisYap = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    // Yeni ve Net Rol Tanımları
    const menuItems = [
        { text: 'Genel Bakış', icon: <DashboardIcon />, roles: ['PERSONEL', 'BIRIM_AMIRI', 'ADMIN'] },
        { text: 'Eğitimlerim', icon: <School />, roles: ['PERSONEL', 'BIRIM_AMIRI', 'ADMIN'] },
        { text: 'Birim Personeli', icon: <People />, roles: ['BIRIM_AMIRI', 'ADMIN'] },
        { text: 'Eğitim Tanımlama', icon: <Engineering />, roles: ['ADMIN'] },
        { text: 'Eğitmen Havuzu', icon: <Assignment />, roles: ['ADMIN'] },
        { text: 'Tüm Raporlar', icon: <History />, roles: ['ADMIN'] },
    ];

    return (
        <Box sx={{ display: 'flex' }}>
            <CssBaseline />
            <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1, backgroundColor: '#1976d2' }}>
                <Toolbar sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Typography variant="h6" noWrap component="div" fontWeight="bold">
                        YTÜ - Hizmet İçi Eğitim Sistemi
                    </Typography>
                    <Button color="inherit" startIcon={<ExitToApp />} onClick={cikisYap}>Güvenli Çıkış</Button>
                </Toolbar>
            </AppBar>

            <Drawer
                variant="permanent"
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box', backgroundColor: '#f8f9fa' },
                }}
            >
                <Toolbar />
                <Box sx={{ overflow: 'auto', mt: 2 }}>
                    <List>
                        {menuItems.map((item) => (
                            item.roles.includes(userRole) && (
                                <ListItem
                                    button
                                    key={item.text}
                                    onClick={() => setAktifMenu(item.text)} // Menüye tıklanınca aktif menüyü değiştirir
                                    sx={{
                                        mb: 1, mx: 1, borderRadius: 1,
                                        backgroundColor: aktifMenu === item.text ? '#e3f2fd' : 'transparent',
                                        color: aktifMenu === item.text ? '#1976d2' : 'inherit'
                                    }}
                                >
                                    <ListItemIcon sx={{ color: aktifMenu === item.text ? '#1976d2' : 'inherit' }}>
                                        {item.icon}
                                    </ListItemIcon>
                                    <ListItemText primary={item.text} primaryTypographyProps={{ fontWeight: aktifMenu === item.text ? 'bold' : 'normal' }}/>
                                </ListItem>
                            )
                        ))}
                    </List>
                </Box>
            </Drawer>

            <Box component="main" sx={{ flexGrow: 1, p: 4 }}>
                <Toolbar />
                <Box sx={{ mb: 4 }}>
                    <Typography variant="h4" fontWeight="bold" color="primary">Hoş Geldiniz, Zeynep İnci</Typography>
                    <Typography variant="subtitle1" color="textSecondary">
                        Sistem Rolü: <span style={{ fontWeight: 'bold' }}>{userRole === 'ADMIN' ? 'Personel Daire Başkanlığı (Yönetici)' : userRole}</span>
                    </Typography>
                </Box>
                <Divider sx={{ mb: 4 }} />

                {/* --- DİNAMİK İÇERİK ALANI (Sadece seçili menü görünür) --- */}

                {aktifMenu === 'Genel Bakış' && (
                    <Typography variant="h6">Sisteme hoş geldiniz. Sol menüden işlem seçebilirsiniz.</Typography>
                )}

                {aktifMenu === 'Eğitim Tanımlama' && userRole === 'ADMIN' && (
                    <EgitimTanimlama />
                )}

                {aktifMenu === 'Birim Personeli' && (userRole === 'BIRIM_AMIRI' || userRole === 'ADMIN') && (
                    <Typography variant="h6">Burası Personel Listesi Tablosu Olacak (Bir sonraki adım)</Typography>
                )}

                {/* Diğer menüler için yer tutucular */}
                {aktifMenu === 'Eğitimlerim' && <Typography variant="h6">Size atanan eğitimler burada listelenecek.</Typography>}
                {aktifMenu === 'Eğitmen Havuzu' && userRole === 'ADMIN' && <Typography variant="h6">Eğitmenler burada listelenecek.</Typography>}
                {aktifMenu === 'Tüm Raporlar' && userRole === 'ADMIN' && <Typography variant="h6">Raporlar burada yer alacak.</Typography>}

            </Box>
        </Box>
    );
}