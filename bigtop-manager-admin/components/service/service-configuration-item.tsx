"use client"

import { Input } from "antd";

const ServiceConfigurationItem = ({ label, type, data }: {
  label: string,
  type: 'input' | 'textarea' | 'radio',
  data: string
}) => {
  return (
    <div className={"mx-8 mb-4 flex justify-between items-center"}>
      <div className={"w-[20%]"}>{label}</div>
      <div className={"w-[60%]"}>
        {type === 'input' && <Input defaultValue={data} />}
        {type === 'textarea' && <Input.TextArea rows={6} defaultValue={data} />}
      </div>
    </div>
  )
}

export default ServiceConfigurationItem;