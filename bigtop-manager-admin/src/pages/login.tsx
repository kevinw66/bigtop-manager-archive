import React from 'react';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {Avatar, Button, Checkbox, Divider, Form, Input} from 'antd';
import logo from '../assets/bigtop.png'
import './login.less'

const App: React.FC = () => {
    const onFinish = (values: any) => {
        console.log('Received values of form: ', values);
    };

    return (
        <Form name="normal_login" className="login-form" initialValues={{remember: true}} onFinish={onFinish}>
            <Avatar size={48} className="login-avatar" src={<img src={logo} alt="avatar"/>}/>
            <p>Bigtop Manager</p>
            <Divider />
            <Form.Item name="username" rules={[{required: true, message: 'Please input your Username!'}]}>
                <Input className="login-form-input" prefix={<UserOutlined className="site-form-item-icon"/>}
                       placeholder="Username"/>
            </Form.Item>
            <Form.Item name="password" rules={[{required: true, message: 'Please input your Password!'}]}>
                <Input className="login-form-input" prefix={<LockOutlined className="site-form-item-icon"/>}
                       type="password" placeholder="Password"/>
            </Form.Item>
            <Form.Item>
                <Button type="primary" htmlType="submit" className="login-form-button">
                    Log in
                </Button>
            </Form.Item>
        </Form>
    );
};

export default App;