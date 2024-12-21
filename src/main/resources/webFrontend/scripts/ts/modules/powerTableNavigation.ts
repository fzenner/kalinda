import { FieldInfo, TdCoord, ROW_IDX_ATTR_NAME, COL_IDX_ATTR_NAME, TD_MAP_TARGET_ATTR_NAME, COLTYPE_ATTR_NAME, Powertable, getWebComponent } from "./Powertable";


const COORD_SPLITTER = "-";




// **********************************************************************
// ***************** ID generation and parsing **************************
// **********************************************************************


export function constructTrId(tableId: string, rowIdx: number) : string {
	let strippedTableId = constructStrippedId(tableId);
	return strippedTableId + "-tr-" + rowIdx;
} 



export function constructRowStateSpanId(table: HTMLTableElement, row: number) {
	let strippedTableId = getStrippedTableId(table)
	return strippedTableId + "-rssp-" + row;
} 

export function constructSelectCheckboxId(table: HTMLTableElement, row: number) {
	let strippedTableId = getStrippedTableId(table)
	return strippedTableId + "-sel-" + row;
} 


/**
 * 
 * @param strippedTableId Should contain no "-" so that split on "-" will work.
 * @param row 
 * @param col 
 * @returns 
 */
export function constructPayloadTdId(table: HTMLTableElement, row: number, col: number) {
	let strippedTableId = getStrippedTableId(table);
	return strippedTableId + "-" + row.toString() + "-" + col.toString();
} 

export function constructPayloadTdIdFast(strippedTableId: string, row: number, col: number) {
	return strippedTableId + "-" + row.toString() + "-" + col.toString();
} 


export function parseTdCoordFromTd(tdElement: HTMLElement) : TdCoord {
	return parseTdCoordFromTdId(tdElement.id);
}


/**
 * We assume that the table id does not contain any minuses. Otherwise,
 * this function only returns the the first part of the table id in the result.
 * @param tdId 
 * @returns 
 */
export function parseTdCoordFromTdId(tdId: string) : TdCoord {
	let row_col = tdId.split(COORD_SPLITTER);
	let length = row_col.length;
	let tableId = row_col[0];
	let rowStr = row_col[length-2];
	let colStr = row_col[length-1];
	let row = parseInt(rowStr, 10);
	let col = parseInt(colStr, 10);
	let tdCoord : TdCoord = {row: row, col: col};
	return tdCoord
}



export function getRowIdxFromTr(trElement: HTMLTableRowElement) {
	let row_col = trElement.id.split(COORD_SPLITTER);
	let rowIdxStr = row_col[row_col.length-1];
	let rowIdxInt : number = parseInt(rowIdxStr);
	return rowIdxInt;
}

export function getRowIdxFromTd(tdElement: HTMLTableCellElement) {
	return tdElement.getAttribute(ROW_IDX_ATTR_NAME);
}

export function getColIdxFromTd(tdElement: HTMLTableCellElement) {
	return tdElement.getAttribute(COL_IDX_ATTR_NAME);
}

export function getSymbolColId(tdElement: HTMLTableCellElement) {
	return tdElement.getAttribute(TD_MAP_TARGET_ATTR_NAME);
}

export function getColType(tdElement: HTMLTableCellElement) {
	return tdElement.getAttribute(COLTYPE_ATTR_NAME);
}



export function constructStrippedId(idWithMinuses: string) : string  {
	let regexp: RegExp = /\-/g;   // Note: This creates a fixed regexp, not a string.
	let strippedId = idWithMinuses.replace(regexp, "X");
	return strippedId;
}





/**
 * Generates the ID for the span element in the state cell / state column
 * @param tableId 
 * @param row 
 * @returns 
 */
export function constructRowStateTdId(table: HTMLTableElement, row: number) {
	let strippedTableId = getStrippedTableId(table);
	return strippedTableId + "-tds-" + row;
} 


export function constructSelectTdId(table: HTMLTableElement, row: number) {
	let strippedTableId = getStrippedTableId(table);
	return strippedTableId + "-co-" + row;
} 



// export function setHeadInfo(table: HTMLTableElement, headInfo: FieldInfo[]) {
// 	table["headInfo"] = headInfo;
// }

// export function getHeadInfo(table: HTMLTableElement) : FieldInfo[] {
// 	return table["headInfo"];
// }

export function getFieldInfoFromTd(td: HTMLTableCellElement) : FieldInfo {
	let coords = parseTdCoordFromTd(td);
	let table = getTableFromChildElement(td);
	let fieldInfo = getHeadInfo(table);
	let result = fieldInfo[coords.col];
	return result;
}

export function setStrippedTableId(table: HTMLTableElement) {
	table["strippedId"] = constructStrippedId(table.id);
}

export function getStrippedTableId(table: HTMLTableElement) :string {
	return table["strippedId"];
}


export function getFieldInfo(fieldName: string, fieldInfArray: FieldInfo[]) : FieldInfo {
	// SPEEDUP: Cashe in hashtable/object
	for (let fieldInfo of fieldInfArray) {
		if (fieldInfo.symbolColId === fieldName) {
			return fieldInfo;
		}
	}
	console.log("Error: No fieldinfor found for kesy: " + fieldName);
}

export function getIndexOfFieldInFieldInfo(key: string, fieldInfArray: FieldInfo[]) : number {
	let idx = 0;
	for (let fieldInfo of fieldInfArray) {
		if (fieldInfo.symbolColId === key) {
			return idx;
		}
		idx++;
	}
	console.log("Error: No fieldinfor found for key: " + key);
}

export function getHeadInfo(htmlTableElement: HTMLTableElement) {
	const powerTable = getPowerTable(htmlTableElement);
	return powerTable.getHeadInfo()
}

export function getPowerTable(htmlTableElement: HTMLTableElement) : Powertable {
	const powerTable = getWebComponent(htmlTableElement) as Powertable;
	return powerTable;
}

export function getTableFromChildElement(childElement: HTMLElement) : HTMLTableElement {
	let result = findFirstParentOfType(childElement, "table") as HTMLTableElement;
	return result;
}


export function getPowertabeFromChildElement(childElement: HTMLElement) : Powertable {
	let tableElement = findFirstParentOfType(childElement, "table") as HTMLTableElement;
	return getPowerTable(tableElement);
}



export function getTrFromChildElement(childElement: HTMLElement) : HTMLTableRowElement {
	let result = findFirstParentOfType(childElement, "tr") as HTMLTableRowElement;
	return result;
}


export function getTdFromChildElement(childElement: HTMLElement) : HTMLTableCellElement {
	let result = findFirstParentOfType(childElement, "td") as HTMLTableCellElement;
	return result;
}


function findFirstParentOfType(childElement: HTMLElement, tagType : string) : HTMLElement {
	let result : HTMLElement;
	let done = false;
	tagType = tagType.toUpperCase();

	let walker = childElement;
	while (true) {
		walker = walker.parentElement;
		if (walker === null) {
			return null;
		}
		if (walker.tagName.toUpperCase() === tagType) {
			result = walker;
			return result as HTMLTableElement;
		}
	}
}



