import { Button, Modal, StepProps, Steps } from "antd";
import React, { Dispatch, SetStateAction, useState } from "react";
import { ExclamationCircleFilled } from "@ant-design/icons";
import SetClusterName from "@/components/cluster/create/set-cluster-name";
import ChooseStack from "@/components/cluster/create/choose-stack";
import Install from "@/components/cluster/create/install";
import Finish from "@/components/cluster/create/finish";
import SetRepository from "@/components/cluster/create/set-repository";
import SetHosts from "@/components/cluster/create/set-hosts";

const ServiceAddModal = ({ open, setOpen }: {
  open: boolean,
  setOpen: Dispatch<SetStateAction<boolean>>
}) => {

  const steps = [
    {
      status: 'process',
      title: 'Choose Services',
      content: <ChooseStack />,
    },
    {
      status: 'wait',
      title: 'Assign Masters',
      content: <SetRepository />,
    },
    {
      status: 'wait',
      title: 'Assign Coodinators',
      content: <SetRepository />,
    },
    {
      status: 'wait',
      title: 'Customize Services',
      content: <SetRepository />,
    },
    {
      status: 'wait',
      title: 'Review',
      content: <SetRepository />,
    },
    {
      status: 'wait',
      title: 'Install',
      content: <SetRepository />,
    },
    {
      status: 'wait',
      title: 'Finish',
      content: <SetRepository />,
    },
  ];

  const [current, setCurrent] = useState(0);
  const [items, setItems] = useState(steps);

  const next = () => {
    items[current].status = 'finish';
    setCurrent(current + 1);
    items[current + 1].status = 'process';
    setItems(items);
  };
  const prev = () => {
    items[current].status = 'wait';
    setCurrent(current - 1);
    items[current - 1].status = 'process';
    setItems(items);
  };

  const handleCancel = () => {
    Modal.confirm({
      title: 'Exit',
      icon: <ExclamationCircleFilled />,
      content: 'Are you sure you want to exit?',
      onOk() {
        setOpen(false);
        setCurrent(0);
        setItems(steps);
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  };

  const handleSubmit = () => {
    setOpen(false);
    setCurrent(0);
    setItems(steps);
  };

  return (
    <Modal width="80%"
      open={open}
      centered={true}
      onCancel={handleCancel}
      maskClosable={false}
      keyboard={false}
      footer={[
        <div key={"footer"}>
          {current > 0 && (
            <Button type="primary" className={"w-1/12"} onClick={() => prev()}>
              Previous
            </Button>
          )}

          {current < items.length - 1 && (
            <Button type="primary" className={"w-1/12"} onClick={() => next()}>
              Next
            </Button>
          )}

          {current === items.length - 1 && (
            <Button type="primary" className={"w-1/12"} onClick={() => handleSubmit()}>
              Done
            </Button>
          )}
        </div>
      ]}
    >
      <div className={"flex flex-row items-center"}>
        <Steps
          direction={"vertical"}
          className={"w-1/4 h-[35rem]"}
          current={current}
          size={"small"}
          items={items as StepProps[]}
        />
        <div className="mt-4 h-[35rem] w-full text-center pl-5 border-l-[1px] border-[#d9d9d9] border-solid">{steps[current].content}</div>
      </div>
    </Modal>
  )
}

export default ServiceAddModal;