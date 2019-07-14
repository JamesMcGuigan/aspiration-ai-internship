#!/usr/bin/env python3
# Rules of the game are that the module submissions must be in the form a single python file

import os
import re

shell_commands = [
    """
    git clone https://github.com/JamesMcGuigan/aspiration-ai-internship;
    cd aspiration-ai-internship;
    git pull
    """,

    """
    cd aspiration-ai-internship/python3/;
    ./requirements.sh;
    source venv/bin/activate; 
    python3 ./module_1/main.py
    """,

    """
    cd aspiration-ai-internship/typescript/;
    npm install -g yarn;
    yarn;       
    yarn start:module_1;
    """,

    """
    cd aspiration-ai-internship/scala/;
    sbt compile;
    sbt 'runMain module_1.Main';
    """,
    
    """
    cd aspiration-ai-internship/data_output/module_1/
    ls -lah;
    tail -n +1 *
    """,
]
for command in shell_commands:
    print( re.sub(r'^|\n', '\n+ ', command) )
    os.system( command )