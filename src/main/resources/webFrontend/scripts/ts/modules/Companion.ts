
export const COMPANION_PROP = "companion"

export interface Companion<T extends HTMLElement> {
    getTopElement() : T;

    /**
     * Establishes the biderectional link
     * @param el 
     * 
     * Typical implementation:
     * ```ts
     * setTopElement(el: T) {
     *     this.topElement = el;
     *     el[COMPANION_PROP] = this;
     * }
     * ```
     * 
     */

    setTopElement(el: T);


    remove() : void;

    
    // topElement?: T;


    // constructor(el: T) {
    //     this.setTopElement(el);
    // }

    

    // setTopElement(el: T) {
    //     this.topElement = el;
    //     el[COMPANION_PROP] = this;
    // }

    



    // remove() : void {
    //     this.topElement.remove();
    // }

}

export function getCompanion<T extends HTMLElement> (el: T) {
    return  el[COMPANION_PROP] as Companion<T>;
}

// /**
//  * Establishes the biderectional link
//  * @param el 
//  */
// export function linkCompanionWithHtmlElement<T extends HTMLElement> (companion: Companion<T>, el: T) {
//         this.topElement = el;
//         el['companion'] = this;
//     }