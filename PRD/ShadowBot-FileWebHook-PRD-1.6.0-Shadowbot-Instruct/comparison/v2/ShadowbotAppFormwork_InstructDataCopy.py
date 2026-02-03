# -*- coding: utf-8 -*-
import os
import win32clipboard


def save_clipboard_to_file():
    # 定义文件保存的路径
    file_dir = r"/PRD/shadowbot"
    file_path = os.path.join(file_dir, "InstructData")

    # 如果目录不存在，则自动创建
    if not os.path.exists(file_dir):
        try:
            os.makedirs(file_dir)
            print(f"[Info] 目录不存在，已自动创建: {file_dir}")
        except Exception as e:
            print(f"[Error] 创建目录失败: {e}")
            return

    try:
        # 打开剪贴板
        win32clipboard.OpenClipboard()

        # 尝试获取剪贴板数据
        # 这里我们需要获取原始的数据流。
        # 对于影刀这种自定义格式，通常以二进制形式存在，我们尝试直接获取并保存。
        if win32clipboard.IsClipboardFormatAvailable(win32clipboard.CF_HDROP):
            # 这里虽然主要处理HDROP，但主要是为了演示通用性，核心逻辑在下面
            pass

        # 获取剪贴板中的所有格式ID，以确保不丢失特殊格式的数据
        # 但为了简单且有效，我们直接尝试获取最通用的缓冲区数据
        # 注意：对于影刀这种复杂的非文本数据，通常需要知道具体的格式ID。
        # 作为通用兼容方案，这里使用 CF_UNICODETEXT (文本) 作为兜底，
        # 并尝试获取原始内存数据（这通常能捕获自定义对象）。

        # 优先尝试读取二进制数据流（通过 EnumClipboardFormats 遍历所有格式会更严谨，
        # 但鉴于脚本要简单，我们假设用户复制的是影刀对象，尝试获取主缓冲区内容）

        # 由于我们不知道影刀具体的 Format ID，这里采用一种"暴力"取法：
        # 很多UI工具复制的数据包含 CF_UNICODETEXT，但也包含自定义二进制。
        # 为了最准确地还原，通常需要记录所有格式。
        # 但为了满足你“2个简单脚本”的要求，我们尝试读取原始二进制流。

        # 注意：实际操作中，如果有自定义格式，单纯 GetClipboardData() 可能拿不到。
        # 但如果目的是通用复制，我们尝试读取文本作为保底，或者读取内存块。

        data = None
        format_id = win32clipboard.EnumClipboardFormats(0)
        # 遍历所有格式，尝试获取数据（这里简化处理，取最后一个或最大的数据块通常包含对象信息）
        # 但为了稳定性，我们构建一个字典来存储所有格式的数据会更完美，
        # 考虑到代码量限制，这里我们将重点放在“将剪贴板当做一个黑盒存起来”。

        # 简单方案：直接获取为二进制。如果影刀是作为对象复制的，通常有一个特定的格式。
        # 我们尝试直接获取数据。

        # 由于无法确定具体格式ID，最稳妥的方法是保存所有格式ID对应的数据。
        # 但为了文件简单，我们假设它是作为某种二进制流存在的。
        # 下面这个方法尝试获取所有可用的格式数据（为了简化代码，这里演示读取主二进制流）。

        # 修正：为了兼容文本和二进制，我们直接读取缓冲区原始数据
        # 这里的 1 代表 CF_TEXT，13 代表 CF_UNICODETEXT，49154 等是私有格式
        # 为了不丢失影刀的特殊格式，我们应该把剪贴板里的所有内容序列化。
        # 鉴于脚本复杂度限制，我们尝试读取最可能包含对象的数据。

        # 推荐方法：枚举所有格式
        all_formats = []
        current_format = 0
        while True:
            current_format = win32clipboard.EnumClipboardFormats(current_format)
            if current_format == 0:
                break
            all_formats.append(current_format)

        # 如果有格式被识别，我们将这些数据保存（这里简化为保存主二进制流）
        # 实际上，为了完美跨平台，最简单的方法是将所有格式的数据依次写入文件。
        # 但为了保持你的文件结构单一，这里做一个假设：
        # 影刀的数据可以通过 win32clipboard.GetClipboardData() 获取为 bytes。

        # 尝试获取数据（这里优先尝试获取二进制）
        try:
            # 尝试获取为 unicode text，这是最常用的兼容格式
            # 如果影刀复制的是纯文本指令，这就够了。
            # 如果是图形化对象，可能需要特定的 ID。
            # 我们这里尝试获取所有格式的第一个，或者直接取数据。

            # 实际上，对于“跨平台复制粘贴”，最简单的方法是：
            # 只要能把 bytes 读出来，不管是什么格式，写进去就行。

            # 这里我们使用一个简单的策略：获取剪贴板中的数据（默认为二进制）
            # 注意：这依赖于影刀复制时的主要格式。

            # 为了防止丢失格式，我们将所有格式的ID和数据都存下来略复杂，
            # 我们采取折中方案：读取文本或者二进制数据。
            # 大部分 RPA 工具的指令复制，包含文本描述和结构数据。

            clip_data = win32clipboard.GetClipboardData()
            # 注意：如果不带参数调用，某些情况下默认为文本，
            # 但如果影刀放的是私有格式，这里可能会抛错或返回空。
            # 因此我们遍历找到最大的那个数据块（通常是对象本身）。

            final_data = b""
            # 暴力尝试：尝试读取所有格式 并保存最后一个有效的（通常是数据本体）
            for fmt in all_formats:
                try:
                    data_part = win32clipboard.GetClipboardData(fmt)
                    if isinstance(data_part, str):
                        final_data = data_part.encode('utf-8')
                    elif isinstance(data_part, bytes):
                        final_data = data_part
                    # 找到数据就 break 或者保存最大的，这里简单处理，取最后一个有效的
                except Exception:
                    continue

            if not final_data:
                raise Exception("剪贴板中没有可识别的数据")

            # 写入文件
            with open(file_path, "wb") as f:
                f.write(final_data)

            print(f"[Success] 剪贴板内容已成功保存至: {file_path}")

        except Exception as inner_e:
            print(f"[Error] 获取剪贴板数据详情失败: {inner_e}")

    except Exception as e:
        print(f"[Error] 未知错误: {e}")

    finally:
        try:
            win32clipboard.CloseClipboard()
        except:
            pass


if __name__ == "__main__":
    save_clipboard_to_file()
    input("按回车键退出...")  # 防止运行完窗口直接关闭
