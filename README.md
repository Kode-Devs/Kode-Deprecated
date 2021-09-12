# Kode

What's New
-------------------

- **5 September 2021**
    - Project build system changed from **Maven** to **Gradle**.
    - Installation Method changed
- **2 November 2020**
    - Objects of Kode and Java are separated out, for clarity and security.

System requirements
-------------------
To install and run Kode, your development environment must meet these minimum requirements:

- [X] **Operating Systems:**
- [X] **Tools:** Kode depends on these tools being available in your environment.
    - [Java SE Development Kit 11](https://www.oracle.com/in/java/technologies/javase-jdk11-downloads.html) or newer

Installation
-------------------

1. Download the following installation bundle to get the latest stable release of the Kode SDK:

   [![Download](https://shields.io/badge/Download%20Now-blue?style=for-the-badge)](https://github.com/Kode-Devs/Kode/archive/refs/heads/stable.zip)
2. Extract the zip file and place the contained `Kode` in the desired installation location for the Kode SDK.

> :warning: **Warning:** Do not install Kode in a directory like `C:\Program Files\` that requires elevated privileges.

If you want to install using [GitHub](https://github.com/), you can get the source code from
the [Kode repo](https://github.com/Kode-Devs/Kode) on GitHub, and change branches or tags as needed. For example:

```bash
C:\src> git clone https://github.com/Kode-Devs/Kode.git -b stable
```

You are now ready to run Kode.


Update your path
-------------------

### In Windows

If you wish to run Kode tools.commands in the regular Windows console, take these steps to add Kode to the `PATH` environment
variable:

- From the Start search bar, enter ‘env’ and select **Edit environment variables for your account**.
- Under **User variables** check if there is an entry called **Path**:
    - If the entry exists, append the full path to `Kode\bin` using `;` as a separator from existing values.
    - If the entry doesn't exist, create a new user variable named `Path` with the full path to `Kode\bin` as its value.

You have to close and reopen any existing console windows for these changes to take effect.

The following command tells you whether the **Kode SDK** is properly associated with your `PATH` variable and hence
ready to use.

```bash
C:\>where.exe kode
C:\path-to-kode-sdk\bin\kode
C:\path-to-kode-sdk\bin\kode.bat
```

### In Linux

You’ll probably want to update this variable permanently, so you can run Kode in any terminal session.

The steps for modifying this variable permanently for all terminal sessions are machine-specific. Typically, you add a
line to a file that is executed whenever you open a new window. For example:

1. Determine the path of your clone of the Kode SDK. You need this in _Step 3_.
2. Open (or create) the `rc` file for your shell. For example, Linux uses the Bash shell by default, so edit
   `$HOME/.bashrc`. If you are using a different shell, the file path and filename will be different on your machine.
3. Add the following line and change `[PATH_OF_KODE_GIT_DIRECTORY]` to be the path of your clone of the Kode git repo:

    ```bash
    export PATH="$PATH:[PATH_OF_KODE_GIT_DIRECTORY]/bin"
    ```

4. Run `source $HOME/.<rc file>` to refresh the current window, or open a new terminal window to automatically source
   the file.
5. Verify that the `Kode/bin` directory is now in your `PATH` by running:

    ```bash
    echo $PATH
    ```

   Verify that the `kode` command is available by running:

    ```bash
    which kode
    ```

In some cases, your distribution may not permanently acquire the path when using the above directions. When this occurs,
you can change the environment variables file directly. These instructions require administrator privileges:

1. Determine the path of your clone of the Kode SDK.
2. Locate the `etc` directory at the root of the system, and open the `profile` file with root privileges.

    ```bash
    sudo nano /etc/profile
    ```

3. Update the PATH string with the location of your Kode SDK directory.

    ```bash
    if [ "`id -u`" -eq 0 ]; then PATH="..."
    else PATH="/usr/local/bin:...:[PATH_OF_KODE_GIT_DIRECTORY]/bin"
    fi export PATH
    ```

4. End the current session or reboot your system.
5. Once you start a new session, verify that the `kode` command is available by running:

    ```bash
    which kode
    ```

### In macOS

You’ll probably want to update this variable permanently, so you can run Kode in any terminal session.

The steps for modifying this variable permanently for all terminal sessions are machine-specific. Typically, you add a
line to a file that is executed whenever you open a new window. For example:

1. Determine the path of your clone of the Kode SDK. You need this in _Step 3_.
2. Open (or create) the `rc` file for your shell. Typing `echo $SHELL` in your Terminal tells you which shell you’re using.
   If you’re using Bash, edit `$HOME/.bash_profile` or `$HOME/.bashrc`. If you’re using Z shell, edit `$HOME/.zshrc`. If
   you’re using a different shell, the file path and filename will be different on your machine.
3. Add the following line and change `[PATH_OF_KODE_GIT_DIRECTORY]` to be the path of your clone of the Kode git repo:

    ```bash
    export PATH="$PATH:[PATH_OF_KODE_GIT_DIRECTORY]/bin"
    ```

4. Run `source $HOME/.<rc file>` to refresh the current window, or open a new terminal window to automatically source
   the file.
5. Verify that the `Kode/bin` directory is now in your `PATH` by running:

    ```bash
    echo $PATH
    ```

   Verify that the `kode` command is available by running:

    ```bash
    which kode
    ```
