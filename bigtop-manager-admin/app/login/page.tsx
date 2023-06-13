"use client"

import React from 'react';
import {LockOutlined, UserOutlined} from '@ant-design/icons';
import {Avatar, Button, Divider, Form, Input} from 'antd';
import {useRouter} from "next/navigation";
import {login} from "@/services/login";
import {AppRouterInstance} from "next/dist/shared/lib/app-router-context";

const App: React.FC = () => {

  const router: AppRouterInstance = useRouter();

  return (
    <Form name="login-form"
          className="flex flex-1 flex-col w-1/3 items-center mt-40 mx-auto rounded-md border-slate-600 border-opacity-5 border-solid border-2"
          initialValues={{remember: true}}
          onFinish={(values) => login(router, values)}
    >

      <div
        className="flex justify-center items-center my-4 mx-auto"
      >

        <Avatar size={48} src={<img src="/logo.png" alt="avatar"/>}/>
        <p
          className="flex justify-center items-center ml-6 text-4xl font-semibold"
        >
          Bigtop Manager
        </p>

      </div>

      <div
        className="flex justify-center items-center font-sm text-slate-400"
      >
        An easy deployment solution for Bigtop
      </div>

      <Divider/>
      <Form.Item name="username" rules={[{required: true, message: 'Please input your Username!'}]}>
        <Input className="login-form-input" prefix={<UserOutlined className="site-form-item-icon"/>}
               placeholder="Username"/>
      </Form.Item>
      <Form.Item name="password" rules={[{required: true, message: 'Please input your Password!'}]}>
        <Input className="login-form-input" prefix={<LockOutlined className="site-form-item-icon"/>}
               type="password" placeholder="Password"/>
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit" className="w-full">
          Log in
        </Button>
      </Form.Item>
    </Form>
  );
};

export default App;