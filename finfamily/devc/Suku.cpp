// VRAEmulator.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include "resource.h"
#include <stdio.h>

#define MAX_LOADSTRING 100

// Global Variables:
HINSTANCE hInst;								// current instance

int APIENTRY WinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPSTR     lpCmdLine,
                     int       nCmdShow)
{
 


	char buffi[1024];
   
   FILE *stream;

	char * java = "java ";
	char * resu;
	buffi[0]='X';
	buffi[1] = 0;

	BOOL jok = FALSE;

	char  path[_MAX_PATH];
	int i;

	memset(path, 0, _MAX_PATH) ;
         
	GetModuleFileName(NULL, path, _MAX_PATH) ;
   
	i = strlen(path);
	if (path[i-3] == 'e' && path[i-2] == 'x' && path[i-1] == 'e') {
		path[i-3]='s';
		path[i-2]='h';
		path[i-1]=0;
	}

	char  dirri[_MAX_PATH];

	strcpy(dirri,path);


	int ix;
	for (ix = i;ix > 0;ix --) {
		if (dirri[ix] == '\\') {
			dirri[ix+1] = 0;
			break;
		}

	}
	BOOL b = SetCurrentDirectory(dirri);
 

	

	stream  = fopen( path, "r" );
	if (stream != NULL) {

		while(TRUE) {
			resu =  fgets(  &buffi[1],  1024, stream ) ;

			if (resu == NULL) {
				break;
			}

			if (strlen(buffi) > 10) {
				if (strncmp(java,&buffi[1],5) == 0) {
					
					strncpy(buffi,"javaw ",5);
					jok = TRUE;
					break;
					
				}

			}

		}
		fclose(stream);


	}

	char* pusku;

	if (jok) {
		pusku = buffi;
		unsigned int i;
		for (i = 0;i < strlen(pusku);i++) {
			if (pusku[i] == '\n') {
				pusku[i] = 0;
				break;
			}

		}

	} else {

		pusku = "javaw -jar suku.jar";
	}


//	LPTSTR path = new TCHAR[_MAX_PATH] ; 
//	char polku [_MAX_PATH];

//	memset(polku, 0, _MAX_PATH) ;
         
//    GetModuleFileName(AfxGetResourceHandle(), path, _MAX_PATH) ;
//	GetModuleFileName(NULL, polku, _MAX_PATH) ;
   
//	int ix = strlen(polku);
//	for 



	PROCESS_INFORMATION pe;
	STARTUPINFO  startinfo;
	ZeroMemory(&startinfo,sizeof(startinfo));
	startinfo.cb = sizeof(startinfo);

	BOOL bRetu = CreateProcess(NULL, pusku,NULL,NULL,FALSE,0,NULL,NULL,&startinfo,&pe);

	

	return FALSE;
	


}








