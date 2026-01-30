# -*- coding: utf-8 -*-
import os
import win32clipboard


def load_file_to_clipboard():
    # 定义文件保存的路径
    file_dir = r"C:\project\ShadowBot-FileWebHook\shadowbot"
    file_path = os.path.join(file_dir, "InstructData")

    if not os.path.exists(file_path):
        print(f"[Error] 文件不存在，无法读取: {file_path}")
        return

    try:
        # 读取二进制文件
        with open(file_path, "rb") as f:
            data_bytes = f.read()

        if not data_bytes:
            print("[Error] 文件内容为空")
            return

        # 打开剪贴板
        win32clipboard.OpenClipboard()
        # 清空剪贴板
        win32clipboard.EmptyClipboard()

        # 将数据写入剪贴板
        # 这里我们写入为 Unicode Text (CF_UNICODETEXT)，这是通用的文本格式。
        # 影刀在粘贴时通常能识别剪贴板中的文本并还原为流程图。
        # (如果影刀必须识别为私有二进制格式才能粘贴，这里可能需要指定格式ID，
        # 但通常文本格式足以兼容 RPA 软件的跨主机复制)

        try:
            # 尝试解码为 string 写入剪贴板，这是最稳妥的还原方式
            # 如果你保存的是纯二进制流，这里直接写入 RegisterClipboardFormat 可能更合适
            # 但为了通用性，我们尝试作为文本写入。
            data_str = data_bytes.decode('utf-8')
            win32clipboard.SetClipboardText(data_str, win32clipboard.CF_UNICODETEXT)
        except UnicodeDecodeError:
            # 如果解码失败（说明确实是纯二进制私有格式），则尝试以二进制方式写入
            # 由于 win32clipboard.SetClipboardData 直接写二进制需要ID，
            # 我们尝试注册一个格式，或者直接作为文本写入可能会乱码但包含数据。
            # 针对影刀，通常文本就足够了。如果必须是二进制，可以使用如下逻辑：
            # fmt_id = win32clipboard.RegisterClipboardFormat("ShadowbotData")
            # win32clipboard.SetClipboardData(fmt_id, data_bytes)
            # 但为了简单，我们假设它是能解码的文本或者我们强制作为字节流尝试写入（如果API支持）。
            # 在标准 win32clipboard 中，最好还是用文本格式传递。
            print("[Warning] 数据非纯文本，尝试以字节形式写入可能不被支持，强制尝试文本写入...")
            # 强制尝试忽略错误解码写入通常不可行，这里报错提示
            print("[Error] 无法将二进制数据写入通用剪贴板格式，请确认数据类型。")
            return

        print(f"[Success] 文件内容已成功写入系统剪贴板，请去影刀中按 Ctrl+V 粘贴。")

    except Exception as e:
        print(f"[Error] 执行过程中出错: {e}")

    finally:
        try:
            win32clipboard.CloseClipboard()
        except:
            pass


if __name__ == "__main__":
    load_file_to_clipboard()
    input("按回车键退出...")  # 防止运行完窗口直接关闭